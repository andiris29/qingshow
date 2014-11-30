var mongoose = require('mongoose');
var async = require('async');
//model
var Show = require('../../model/shows');
var People = require('../../model/peoples');
//util
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');

var _like = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.currentUser._id);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }

    RelationshipHelper.create(RPeopleLikeShow, initiatorRef, affectedRef, ResponseHelper.generateGeneralCallback(res));
};

var _unlike = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.currentUser._id);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }

    RelationshipHelper.remove(RPeopleLikeShow, initiatorRef, affectedRef, ResponseHelper.generateGeneralCallback(res));
};

module.exports = {
    'follow' : {
        method : 'post',
        func : _like,
        needLogin : true
    },
    'unfollow' : {
        method : 'post',
        func : _unlike,
        needLogin : true
    }
};
