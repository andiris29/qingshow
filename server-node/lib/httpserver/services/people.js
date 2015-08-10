var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
//util
var ServiceHelper = require('../helpers/ServiceHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var people = module.exports;

people.queryFollowers= {
    method : 'get',
    func : function(req, res) {
        ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowPeople, {
            'query' : 'targetRef',
            'result' : 'initiatorRef'
        });
    }
};

people.queryFollowingPeoples= {
    method : 'get',
    func : function(req, res) {
        ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowPeople, {
            'query' : 'initiatorRef',
            'result' : 'targetRef'
        });
    }
};

people.follow = {
    method : 'post',
    permissionValidators : ['loginValidator'],
    func : function(req, res) {
        try {
            var param = req.body;
            var targetRef = RequestHelper.parseId(param._id);
            var initiatorRef = RequestHelper.parseId(req.qsCurrentUserId);

            RelationshipHelper.create(RPeopleFollowPeople, initiatorRef, targetRef, function(err) {
                ResponseHelper.response(res, err);
            });
        } catch (e) {
            ResponseHelper.response(res, ServerError.PeopleNotExist);
            return;
        }
    }
};

people.unfollow = {
    method : 'post',
    permissionValidators : ['loginValidator'],
    func: function(req, res) {
        try {
            var param = req.body;
            var targetRef = RequestHelper.parseId(param._id);
            var initiatorRef = RequestHelper.parseId(req.qsCurrentUserId);

            RelationshipHelper.remove(RPeopleFollowPeople, initiatorRef, targetRef, function(err) {
                ResponseHelper.response(res, err);
            });
        } catch (e) {
            ResponseHelper.response(res, ServerError.PeopleNotExist);
            return;
        }
    }
};

people.query = {
    method : 'get',
    func : function(req, res) {
        var _ids;
        async.waterfall([
        function(callback) {
            // Parser req
            try {
                _ids = RequestHelper.parseIds(req.queryString._ids);
                callback(null);
            } catch (err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            // Query & populate
            People.find({
                '_id' : {
                    '$in' : _ids
                }
            }).exec(callback);
        },
        function(peoples, callback) {
            ContextHelper.appendPeopleContext(req.qsCurrentUserId, peoples, callback);
        }], function(err, peoples) {
            ResponseHelper.response(res, err, {
                'peoples' : peoples 
            });
        });
    }
};
