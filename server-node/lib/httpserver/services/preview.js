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

var _feed = function(req, res, previewFinder, queryStringParser, beforeResponseEnd) {
    var pageNo, pageSize, numTotal;
    async.waterfall([
    function(callback) {
        try {
            pageNo = parseInt(req.queryString.pageNo || 1);
            pageSize = parseInt(req.queryString.pageSize || 10);
            var qsParam = queryStringParser ? queryStringParser(req.queryString) : null;
            callback(null, qsParam);
        } catch(err) {
            callback(ServerError.fromError(err));
        }
    },
    function(qsParam, callback) {
        previewFinder(pageNo, pageSize, qsParam, function(err, count, previews) {
            numTotal = count;
            if (!err && previews.length === 0) {
                err = ServerError.PagingNotExist;
            }
            callback(err, previews);
        });
    }, _parseCover,
    function(previews, callback) {
        ContextHelper.appendPreviewContext(req.qsCurrentUserId, previews, callback);
    }], function(err, previews) {
        // Response
        ResponseHelper.responseAsPaging(res, err, {
            'previews' : previews
        }, pageSize, numTotal, beforeResponseEnd);
    });
};

preview.feed = {
    'method' : 'get',
    'func' : function(req, res) {
        var chosen;
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
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
                var skip = (pageNo - 1) * pageSize;
                chosen = new PreviewChosen({
                    'activateTime' : chosen.activateTime,
                    'previewRefs' : chosen.previewRefs.filter(function(preview, index) {
                        return index >= skip && index < skip + pageSize;
                    })
                });
                PreviewChosen.populate(chosen, {
                    'path' : 'previewRefs'
                }, function(err, chosen) {
                    callback(err, count, chosen.previewRefs);
                });
            }], callback);
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
        var pageNo, pageSize, numTotal;
        var _id;
        async.waterfall([
        function(callback) {
            // Parse request
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 20);
                _id = mongoose.mongo.BSONPure.ObjectID(param._id);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            // Query
            var criteria = {
                'targetRef' : _id,
                'delete' : null
            };
            MongoHelper.queryPaging(PreviewComment.find(criteria).sort({
                'create' : -1
            }).populate('authorRef').populate('atRef'), PreviewComment.find(criteria), pageNo, pageSize, function(err, count, previewComments) {
                numTotal = count;
                callback(err, previewComments);
            });
        }], function(err, previewComments) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'previewComments' : previewComments
            }, pageSize, numTotal);
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
