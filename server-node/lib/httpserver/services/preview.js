var mongoose = require('mongoose');
var async = require('async');
//model
var Preview = require('../../model/previews');
var People = require('../../model/peoples');
var PreviewComment = require('../../model/previewComments');
var RPeopleLikePreview = require('../../model/rPeopleLikePreview');
var PreviewChosen = require('../../model/previewChosens');
//util
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var preview = module.exports;

/**
 * Ignore error
 *
 * @param {Object} previews
 * @param {Object} callback
 */
var _parseCover = function(previews, callback) {
    var tasks = [];
    previews.forEach(function(preview) {
        tasks.push(function(callback) {
            preview.updateCoverMetaData(function(err) {
                callback(null, preview);
            });
        });
    });
    async.parallel(tasks, callback);
};

preview.feed = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            async.waterfall([
            function(callback) {
                // Query chosen
                PreviewChosen.find().where('activateTime').lte(Date.now()).sort({
                    'activateTime' : 1
                }).limit(1).exec(function(err, chosens) {
                    if (err) {
                        callback(ServerError.fromDescription(err));
                    } else if (!chosens || !chosens.length) {
                        callback(ServerError.fromCode(ServerError.PreviewNotExist));
                    } else {
                        chosen = chosens[0];
                        callback(null, chosen.previewRefs.length);
                    }
                });
            },
            function(count, callback) {
                // Query previews
                var skip = (qsParam.pageNo - 1) * qsParam.pageSize;
                chosen = new PreviewChosen({
                    'activateTime' : chosen.activateTime,
                    'previewRefs' : chosen.previewRefs.filter(function(preview, index) {
                        return index >= skip && index < skip + qsParam.pageSize;
                    })
                });
                PreviewChosen.populate(chosen, {
                    'path' : 'previewRefs'
                }, function(err, chosen) {
                    callback(err, chosen.previewRefs, count);
                });
            }], callback);
        }, function(models) {
            // responseDataBuilder
            return {
                'previews' : models
            };
        }, {
            'afterQuery' : function(qsParam, currentPageModels, numTotal, callback) {
                async.series([
                function(callback) {
                    _parseCover(currentPageModels, callback);
                },
                function(callback) {
                    ContextHelper.appendPreviewContext(req.qsCurrentUserId, currentPageModels, callback);
                }], callback);
            }
        });
    }
};

preview.like = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var targetRef, initiatorRef;
        async.waterfall([
        function(callback) {
            try {
                var param = req.body;
                targetRef = RequestHelper.parseId(param._id);
                initiatorRef = req.qsCurrentUserId;
            } catch (err) {
                callback(err);
            }
            callback();
        },
        function(callback) {
            // Like
            RelationshipHelper.create(RPeopleLikePreview, initiatorRef, targetRef, function(err, relationship) {
                callback(err);
            });
        },
        function(callback) {
            // Count
            Preview.update({
                '_id' : targetRef
            }, {
                '$inc' : {
                    'numLike' : 1
                }
            }, function(err, numUpdated) {
                callback(err);
            });
        }], function(err) {
            ResponseHelper.response(res, err);
        });

    }
};

preview.unlike = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
            var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
        } catch (e) {
            ResponseHelper.response(res, ServerError.RequestValidationFail);
            return;
        }

        RelationshipHelper.remove(RPeopleLikePreview, initiatorRef, targetRef, function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

preview.queryComments = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            var criteria = {
                'targetRef' : qsParam._id,
                'delete' : null
            };
            MongoHelper.queryPaging(PreviewComment.find(criteria).sort({
                'create' : -1
            }).populate('authorRef').populate('atRef'), PreviewComment.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(models) {
            // responseDataBuilder
            return {
                'previewComments' : models
            };
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : mongoose.mongo.BSONPure.ObjectID(raw._id)
                };
            }
        });
    }
};

preview.comment = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
            var atRef = mongoose.mongo.BSONPure.ObjectID(param._atId);
            var comment = param.comment;
        } catch (err) {
            ResponseHelper.response(res, err);
            return;
        }
        async.waterfall([
        function(callback) {
            var previewComment = new PreviewComment({
                'targetRef' : targetRef,
                'atRef' : atRef,
                'authorRef' : req.qsCurrentUserId,
                'comment' : comment
            });
            previewComment.save(function(err) {
                callback();
            });
        }], function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

preview.deleteComment = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var _id = mongoose.mongo.BSONPure.ObjectID(param._id);
        } catch (err) {
            ResponseHelper.response(res, err);
            return;
        }
        async.waterfall([
        function(callback) {
            PreviewComment.findOne({
                '_id' : _id,
                'authorRef' : req.qsCurrentUserId,
                'delete' : null
            }, callback);
        },
        function(comment, callback) {
            if (comment) {
                comment.set('delete', new Date());
                comment.save(function(err) {
                    callback();
                });
            } else {
                callback();
            }
        }], function(err) {
            ResponseHelper.response(res, err);
        });
    }
};
