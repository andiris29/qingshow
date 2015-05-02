var mongoose = require('mongoose');
var async = require('async');
//model
var Item = require('../../model/items');
var RPeopleLikeItem = require('../../model/rPeopleLikeItem');
//util
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var item = module.exports;

item.like = {
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
            RelationshipHelper.create(RPeopleLikeItem, initiatorRef, targetRef, function(err, relationship) {
                callback(err);
            });
        },
        function(callback) {
            // Count
            Item.update({
                '_id' : targetRef
            }, {
                '$inc' : {
                    'numLike' : 1
                }
            }, function(err, numUpdated) {
                callback(err);
            }
        }], function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

item.unlike = {
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
            RelationshipHelper.remove(RPeopleLikeItem, initiatorRef, targetRef, function(err, relationship) {
                callback(err);
            });
        },
        function(callback) {
            // Count
            Item.update({
                '_id' : targetRef
            }, {
                '$inc' : {
                    'numLike' : -1
                }
            }, function(err, numUpdated) {
                callback(err);
            }
        }], function(err) {
            ResponseHelper.response(res, err);
        });
    }
};
