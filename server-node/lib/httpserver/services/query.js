var mongoose = require('mongoose');
var async = require('async');
//Model
var Show = require('../../model/shows');
//Utils
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');
var ResponseHelper = require('../helpers/ResponseHelper');

var _shows = function(req, res) {
    var param = req.queryString;
    try {
        var _ids = ServicesUtil.stringArrayToObjectIdArray(param._ids.split(','));
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }
    async.waterfall([
    function(callback) {
        Show.find({
            '_id' : {
                '$in' : _ids
            }
        }).populate('modelRef').populate('itemRefs').exec(callback);
    },
    function(shows, callback) {
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
        method : 'get',
        func : _shows,
        needLogin : false
    }
};
