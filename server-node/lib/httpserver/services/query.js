var mongoose = require('mongoose');
var async = require('async');
//Model
var Show = require('../../model/shows');
//Utils
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');
var ResponseHelper = require('../helpers/ResponseHelper');
var ContextHelper = require('../helpers/ContextHelper');

var _shows = function(req, res) {
    async.waterfall([
    function(callback) {
        // Parser req
        try {
            callback(null, {
                '_ids' : ServicesUtil.stringArrayToObjectIdArray(req.queryString._ids.split(','))
            });
        } catch (e) {
            ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        }
    },
    function(qsParams, callback) {
        // Query & populate
        Show.find({
            '_id' : {
                '$in' : qsParams._ids
            }
        }).populate('modelRef').populate('itemRefs').exec(callback);
    },
    function(shows, callback) {
        // Append followed by current user
        ContextHelper.likedByCurrentUser(req.qsCurrentUserId, shows, callback);
    },
    function(shows, callback) {
        // Populate nested references
        Show.populate(shows, {
            'path' : 'itemRefs.brandRef',
            'model' : 'brands'
        }, callback);
    }], ResponseHelper.generateGeneralCallback(res, function(result) {
        return {
            'shows' : result
        };
    }));
};

module.exports = {
    'shows' : {
        'method' : 'get',
        'func' : _shows,
        'needLogin' : false
    }
};
