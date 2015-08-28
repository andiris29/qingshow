var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../model/peoples');
var jPushAudiences = require('../../model/jPushAudiences');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');
var BonusHelper = require('../helpers/BonusHelper');
var ServerError = require('../server-error');

var userBonus = model.exports;

userBonus.forge = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        async.waterfall([
        function(callback) {
            Item.findOne({
                '_id' : RequestHelper.parseId(param.itemRef)
            }).exec(function(err, item) {
                if (err) {
                    callback(err);
                } else if (!item) {
                    callback(ServerError.ItemNotExist);
                } else {
                    callback(null, item);
                }
            });
        },
        function(item, callback) {
            BonusHelper.updateBonuse(RequestHelper.parseId(param.promoterRef), {
                forgerRef : req.qsCurrentUserId
            },  (item.promoPrice * 0.9), item.name, callback);
        }], function(error, people) {
            ResponseHelper.response(res, error, {
                'people' : people
            });
        });
    }
};
