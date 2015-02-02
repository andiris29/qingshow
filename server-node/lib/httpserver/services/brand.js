var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
//model
var People = require('../../model/peoples');
var Brand = require('../../model/brands');
var RPeopleFollowBrand = require('../../model/rPeopleFollowBrand');
//util
var ServiceHelper = require('../helpers/ServiceHelper');
var ContextHelper = require('../helpers/ContextHelper');
var MongoHelper = require('../helpers/MongoHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var brands = module.exports;

brands.query = {
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
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            // Query & populate
            Brand.find({
                '_id' : {
                    '$in' : _ids
                }
            }).exec(callback);
        },
        function(brands, callback) {
            // Append followed by current user
            ContextHelper.appendBrandContext(req.qsCurrentUserId, brands, callback);
        }], function(err, brands) {
            ResponseHelper.response(res, err, {
                'brands' : brands
            });
        });
    }
};

brands.queryBrands = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            var criteria = {};
            if (qsParam.type !== undefined) {
                criteria.type = qsParam.type;
            }
            // Query
            MongoHelper.queryRandom(Brand.find(criteria), Brand.find(criteria), qsParam.pageSize, function(err, models) {
                callback(err, models, qsParam.pageNo * qsParam.pageSize + 1);
            });
        }, function(models) {
            // responseDataBuilder
            return {
                'brands' : models
            };
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    'type' : RequestHelper.parseNumber(raw.type)
                };
            },
            'afterQuery' : function(qsParam, currentPageModels, numTotal, callback) {
                async.series([
                function(callback) {
                    MongoHelper.updateCoverMetaData(currentPageModels, callback);
                },
                function(callback) {
                    ContextHelper.appendBrandContext(req.qsCurrentUserId, currentPageModels, callback);
                }], callback);
            }
        });
    }
};
brands.queryFollowers = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowBrand, {
            'query' : 'targetRef',
            'result' : 'initiatorRef'
        });
    }
};

brands.follow = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
            var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
        } catch (e) {
            ResponseHelper.response(res, ServerError.PeopleNotExist);
            return;
        }

        RelationshipHelper.create(RPeopleFollowBrand, initiatorRef, targetRef, function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

brands.unfollow = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
            var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
        } catch (e) {
            ResponseHelper.response(res, ServerError.PeopleNotExist);
            return;
        }
        RelationshipHelper.remove(RPeopleFollowBrand, initiatorRef, targetRef, function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

brands.queryFollowed = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryRelatedBrands(req, res, RPeopleFollowBrand, {
            'query' : 'initiatorRef',
            'result' : 'targetRef'
        });
    }
};

