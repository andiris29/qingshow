var mongoose = require('mongoose');
var async = require('async');
// Models
var ShowComments = require('../../model/showComments');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
var RPeopleShareShow = require('../../model/rPeopleShareShow');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
var RPeopleCreateShow = require('../../model/rPeopleCreateShow');
var People = require('../../model/peoples');

/**
 * ContextHelper
 *
 * Input models then output models with context
 */
var ContextHelper = module.exports;

ContextHelper.appendPeopleContext = function(qsCurrentUserId, peoples, callback) {
    peoples = _prepare(peoples);

    // __context.followedByCurrentUser
    var followedByCurrentUser = function(callback) {
        _rInitiator(RPeopleFollowPeople, qsCurrentUserId, peoples, 'followedByCurrentUser', callback);
    };

    // __context.numFollowPeoples
    var numFollowPeoples = function(callback) {
        _numAssociated(peoples, RPeopleFollowPeople, 'initiatorRef', 'numFollowPeoples', callback);
    };
    // __context.numFollowers
    var numFollowers = function(callback) {
        _numAssociated(peoples, RPeopleFollowPeople, 'targetRef', 'numFollowers', callback);
    };

    async.parallel([followedByCurrentUser, numFollowPeoples, numFollowers], function(err) {
        callback(null, peoples);
    });
};

ContextHelper.appendShowContext = function(qsCurrentUserId, shows, callback) {
    shows = _prepare(shows);
    // __context.numComments
    var numComments = function(callback) {
        _numAssociated(shows, ShowComments, 'targetRef', 'numComments', callback);
    };
    // __context.likedByCurrentUser
    var likedByCurrentUser = function(callback) {
        _rInitiator(RPeopleLikeShow, qsCurrentUserId, shows, 'likedByCurrentUser', callback);
    };
    // __context.sharedByCurrentUser
    var sharedByCurrentUser = function(callback) {
        _rInitiator(RPeopleShareShow, qsCurrentUserId, shows, 'sharedByCurrentUser', callback);
    };

    // __context.likeDate
    var likeDate = function(callback) {
        _rCreateDate(RPeopleLikeShow, qsCurrentUserId, shows, 'likeDate', callback);
    };

    // __context.promotionRef
    var generatePromoInfo = function(callback) {
        _generatePromoInfo(qsCurrentUserId, shows, 'promotionRef', callback);
    };

    // __context.createBy
    var generateCreateBy = function(callback) {
        _initiator(RPeopleCreateShow, People, shows, 'createdBy', callback);
    };

    // modedRef.__context.followedByCurrentUser
    async.parallel([numComments, likedByCurrentUser, sharedByCurrentUser, likeDate, generatePromoInfo, generateCreateBy], function (err) {
        callback(null, shows);
    });
};

var _prepare = function(models) {
    return models.filter(function(model) {
        return model;
    }).map(function(model) {
        model.__context = model.__context || {};
        return model;
    });
};

var _numAssociated = function(models, RModel, associateField, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            var criteria = {
                'delete' : null
            };
            criteria[associateField] = model._id;
            RModel.count(criteria, function(err, count) {
                model.__context[contextField] = count || 0;
                callback(null);
            });
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _rInitiator = function(RModel, initiatorRef, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            if (initiatorRef) {
                RModel.findOne({
                    'initiatorRef' : initiatorRef,
                    'targetRef' : model._id
                }, function(err, relationship) {
                    model.__context[contextField] = Boolean(!err && relationship);
                    callback();
                });
            } else {
                model.__context[contextField] = false;
                callback();
            }
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _rCreateDate = function(RModel, initiatorRef, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            if (initiatorRef) {
                RModel.findOne({
                    'initiatorRef' : initiatorRef,
                    'targetRef' : model._id
                }, function(err, relationship) {
                    if (Boolean(!err && relationship)) {
                        model.__context[contextField] = relationship.create;
                    }
                    callback();
                });
            } else {
                callback();
            }
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _generatePromoInfo =  function(peopleId, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            model.__context[contextField] = {};
            if (model.promotionRef === null || model.promotionRef === undefined) {
                model.__context[contextField].enabled = false;
                callback();
                return;
            }
            if (model.promotionRef.criteria === 0) {
                // 分享后可获得优惠
                RPeopleShareShow.findOne({
                    'initiatorRef' : peopleId,
                    'targetRef' : model._id
                }, function(err, relationship) {
                    model.__context[contextField].enabled = Boolean(!err && relationship);
                    callback();
                });
            } else {
                // 其他策略
                model.__context[contextField].enabled = false;
                callback();
            }
        };
    });

    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _initiator = function(RModel, InitiatorModel, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            model.__context[contextField] = {};
            
            RModel.findOne({
                'targetRef' : model._id
            }, function(err, relationship) {
                if (!err && relationship) {
                    InitiatorModel.findOne({
                        '_id' : relationship.initiatorRef
                    }, function(err, people) {
                        model.__context[contextField] = Boolean(!err && people) ? people : {};
                        callback();
                    });
                } else {
                    callback();
                }
            });
        };
    });

    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};
