
var async = require('async');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');

var GoblinScheduler = require('../../scheduled/goblin/scheduler/GoblinScheduler');

var goblin = module.exports;

goblin.nextItem = {
    method : 'post',
    func : function (req, res) {
        var param = req.body;
        var type;
        if (param.type) {
            type = parseInt(param.type);
        }
        async.waterfall([
            function (callback) {
                GoblinScheduler.nextItem(type, callback);
            }
        ], function (err, item) {
            ResponseHelper.response(res, err, {
                item : item
            });
        });
    }
};

goblin.crawlItemComplete = {
    method : 'post',
    func : function (req, res) {
        var param = req.body;
        var itemIdStr = param.itemId;

    }
};