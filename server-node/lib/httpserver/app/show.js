var mongoose = require('mongoose');
var async = require('async');
//model
var Show = require('../../dbmodels').Show;
var Item = require('../../dbmodels').Item;
var ShowComment = require('../../dbmodels').ShowComment;
var RPeopleLikeShow = require('../../dbmodels').RPeopleLikeShow;
var People = require('../../dbmodels').People;
var jPushAudiences = require('../../dbmodels').JPushAudience;
var RPeopleViewShow = require('../../dbmodels').RPeopleViewShow;

//util
var MongoHelper = require('../../helpers/MongoHelper');
var ContextHelper = require('../../helpers/ContextHelper');
var RelationshipHelper = require('../../helpers/RelationshipHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var RequestHelper = require('../../helpers/RequestHelper');
var NotificationHelper = require('../../helpers/NotificationHelper');
var TraceHelper = require('../../helpers/TraceHelper');

var errors = require('../../errors');

var show = module.exports;

show.query = {
    'method' : 'get',
    'func' : function(req, res) {
        var _ids;
        async.waterfall([
        function(callback) {
            // Parser req
            try {
                _ids = RequestHelper.parseIds(req.queryString._ids);
                callback(null);
            } catch (err) {
                callback(errors.NotEnoughParam);
            }
        },
        function(callback) {
            // Query & populate
            Show.find({
                '_id' : {
                    '$in' : _ids
                }
            }).populate('ownerRef').populate('itemRefs').exec(callback);
        },
        function(shows, callback) {
            var itemRefs = [];
            shows.forEach(function(show) {
                itemRefs = itemRefs.concat(show.itemRefs);
            });
            Item.populate(itemRefs, {
                'path' : 'shopRef',
                'model' : 'peoples'
            }, function() {
                callback(null, shows);
            });
        },
        function(shows, callback) {
            // Append followed by current user
            ContextHelper.appendShowContext(req.qsCurrentUserId, shows, callback);
        }], function(err, shows) {
            ResponseHelper.response(res, err, {
                'shows' : shows
            });
            if (shows.length === 1) {
                var show = shows[0];
                // Log
                TraceHelper.trace('behavior-show-query', req, {
                    '_showId' : show._id.toString()
                });
            }

        });
    }
};

show.like = {
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
            RelationshipHelper.create(RPeopleLikeShow, initiatorRef, targetRef, function(err, relationship) {
                callback(err);
            });
        },
        function(callback) {
            // Count
            Show.update(
                {'_id' : targetRef},
                {'$inc' : {'numLike' : 1}},
                function(err, numUpdated) {
                    callback(err);
                }
            );
        }], function(err) {
            ResponseHelper.response(res, err);
        });

    }
};

show.unlike = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = RequestHelper.parseId(param._id);
            var initiatorRef = req.qsCurrentUserId;
        } catch (e) {
            ResponseHelper.response(res, errors.RequestValidationFail);
            return;
        }

        RelationshipHelper.remove(RPeopleLikeShow, initiatorRef, targetRef, function(err) {
            Show.update(
                {'_id' : targetRef},
                {'$inc' : {'numLike' : -1}},
                function(err, numUpdated) {
                    ResponseHelper.response(res, err);
                }
            );
        });
    }
};

show.queryComments = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            var criteria = {
                'targetRef' : qsParam._id,
                'delete' : null
            };
            MongoHelper.queryPaging(ShowComment.find(criteria).sort({
                'create' : -1
            }).populate('authorRef').populate('atRef'), ShowComment.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(models) {
            // responseDataBuilder
            return {
                'showComments' : models
            };
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : mongoose.Types.ObjectId(raw._id)
                };
            }
        });

    }
};

show.comment = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.Types.ObjectId(param._id);
            var atRef = mongoose.Types.ObjectId(param._atId);
            var comment = param.comment;
        } catch (err) {
            ResponseHelper.response(res, err);
            return;
        }
        async.waterfall([
        function(callback) {
            var showComment = new ShowComment({
                'targetRef' : targetRef,
                'atRef' : atRef,
                'authorRef' : req.qsCurrentUserId,
                'comment' : comment
            });
            showComment.save(function(err) {
                callback();
            });
        }, 
        function(callback) {
            Show.findOne({
                '_id' : targetRef
            }).populate('ownerRef').exec(function(err, show) {
                if (show && show.ownerRef && !show.hideAgainstOwner) {
                    if (show.ownerRef._id.toString() != req.qsCurrentUserId.toString()) {
                       NotificationHelper._push([show.ownerRef], NotificationHelper.MessageNewShowComment, {
                            '_id' : param._id,
                            'command' : NotificationHelper.CommandNewShowComments
                        }, null);
                    }
                }
            }); 
            callback();
        }], function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

show.deleteComment = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var _id = mongoose.Types.ObjectId(param._id);
        } catch (err) {
            ResponseHelper.response(res, err);
            return;
        }
        async.waterfall([
        function(callback) {
            ShowComment.findOne({
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

show.view = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Show, '_id', 'showRef'),
        function(req, res, next) {
            var show = req.injection.showRef;
            if (show.ownerRef.toString() === req.qsCurrentUserId.toString()) {
                next(errors.AlreadyRelated);
                return;
            }
            RelationshipHelper.create(RPeopleViewShow, req.qsCurrentUserId, show._id, function(err) {
                if (err) {
                    next(err);
                } else {
                    show.numView++;
                    show.save(function(err) {
                        ResponseHelper.writeData(res, show);
                        next(err);
                    });
                }
            });
        }
    ]
};

