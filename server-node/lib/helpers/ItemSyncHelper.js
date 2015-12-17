var async = require('async');

var Item = require('../dbmodels').Item;

var errors = require('../errors');

var ItemSyncHelper = module.exports;

var ONE_MINUTE = 1 * 60 * 1000;
var ONE_HOUR = 1 * 3600 * 1000;
var ONE_DAY = 24 * 3600 * 1000;

ItemSyncHelper.request = function(item) {
    var now = new Date();
    if (!item.sync || new Date().getTime() - item.sync.getTime() > ONE_DAY) {
        item.syncRequestAt = now;
        item.save();
    }
};
