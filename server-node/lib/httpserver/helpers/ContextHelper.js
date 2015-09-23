var mongoose = require('mongoose');
var async = require('async');
// Models
var Shows = require('../../models').Show;
var ShowComments = require('../../models').ShowComment;
var RPeopleLikeShow = require('../../models').RPeopleLikeShow;
var RPeopleShareShow = require('../../models').RPeopleShareShow;
var RPeopleFollowPeople = require('../../models').RPeopleFollowPeople;
var RPeopleShareTrade = require('../../models').RPeopleShareTrade;
var People = require('../../models').People;
var Item = require('../../models').Item;

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
    // __context.numCreateShows, __context.numLikeToCreateShows
    var numCreateShows = function(callback) {
        var peopleMap = {};
        Shows.aggregate([{
            '$match' : {
                '$or' : peoples.map(function(people) {
                    peopleMap[people._id.toString()] = people;
                    return {
                        'ownerRef' : people._id
                    };
                })
            }
        }, {
            '$group' : {
                '_id' : '$ownerRef',
                'numCreateShows' : {
                    '$sum' : 1
                },
                'numLikeToCreateShows' : {
                    '$sum' : '$numLike'
                }
            }
        }], function(err, results) {
            if (!err) {
                results.forEach(function(result) {
                    var people = peopleMap[result._id];
                    people.__context.numCreateShows = result.numCreateShows;
                    people.__context.numLikeToCreateShows = result.numLikeToCreateShows;
                });
            }
            callback(null, peoples);
        });
    };

    async.parallel([followedByCurrentUser, numFollowPeoples, numFollowers, numCreateShows], function(err) {
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

    // modedRef.__context.followedByCurrentUser
    async.parallel([numComments, likedByCurrentUser, sharedByCurrentUser], function(err) {
        callback(null, shows);
    });
};

ContextHelper.appendTradeContext = function(qsCurrentUserId, trades, callback) {
    trades = _prepare(trades);

    // __context.sharedByCurrentUser
    var sharedByCurrentUser = function(callback) {
        _rInitiator(RPeopleShareTrade, qsCurrentUserId, trades, 'sharedByCurrentUser', callback);
    };

    var itemDelist = function(callback) {
        _item(Item, trades, 'delist', callback);
    };

    var itemExpectablePrice = function(callback) {
        _item(Item, trades, 'expectablePrice', callback);
    };

    async.parallel([sharedByCurrentUser, itemDelist, itemExpectablePrice], function(err) {
        callback(null, trades);
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

var _item = function(InitiatorModel, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            if (!model.__context.item) {
                model.__context.item = {};
            }
            model.__context.item[contextField] = {};                

            InitiatorModel.findOne({
                '_id' : model.itemRef
            }, function(err , relationship) {
                 if (Boolean(!err && relationship)) {
                    console.log(relationship[contextField]);
                    if (relationship[contextField]) { 
                        model.__context.item[contextField] = relationship[contextField];
                    };
                   
                    callback();
                 } else {
                    callback();
                 }
            })
        }
    });

    async.parallel(tasks, function(err) {
        callback(null, models);
    })
}