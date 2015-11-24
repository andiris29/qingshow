
var async = require('async');

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var GoblinScheduler = require('./goblin/GoblinScheduler'),
    ItemSyncService = require('./goblin/ItemSyncService'),
    GoblinLogger = require('./goblin/GoblinLogger');
var GoblinError = require('../../goblin-common/GoblinError');
var Item = require('../../dbmodels').Item;
var errors = require('../../errors');

var winston = require('winston');

var goblin = module.exports;

goblin.nextItem = {
    method : 'post',
    func : function (req, res) {
        if (req.body.version !== global.qsConfig.goblin.overseer.supportedVersion) {
            ResponseHelper.response(res, errors.GoblinSlaveDisabled);
            return;
        }
        
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
            if (err && err.domain === GoblinError.Domain && err.errorCode === GoblinError.NoItemShouldBeCrawl) {
                //TODO refactor
                err = errors.genGoblin('genGoblin', err);
            }
            ResponseHelper.response(res, err, {
                item : item
            });
            if (item) {
                GoblinLogger.assign(item, req);
            }
        });
    }
};

goblin.crawlItemComplete = {
    method : 'post',
    func : function (req, res) {
        var param = req.body;
        var itemIdStr = param.itemId;
        var itemInfo = param.itemInfo;
        var error = param.error;

        async.waterfall([
            function (callback) {
                Item.findOne({
                    _id : RequestHelper.parseId(itemIdStr)
                }, callback);
            }, function (item, callback) {
                if (!item) {
                    callback(errors.ItemNotExist);
                } else {
                    callback(null, item);
                }
            }, function (item, callback) {
                ItemSyncService.syncItemInfo(item, itemInfo, error, callback);
            }
        ], function (err, item) {
            GoblinScheduler.finishItem(item._id, error || err, function(){});
            if (!err) {
                if (itemInfo) {
                    GoblinLogger.complete(item, req);
                } else {
                    GoblinLogger.delist(item, req);
                }
            } else {
                GoblinLogger.error(item, req, err);
            }
            ResponseHelper.response(res, err, {
                item : item
            });
        });

    }
};

goblin.crawlItemFailed = {
    method : 'post',
    func : function (req, res) {
        GoblinLogger.error(item, req, req.body.log);
        
        ResponseHelper.response(res, null, {});
    }
};

goblin.findItem = {
    method: 'get',
    func: function(req, res) {
        var param = req.queryString;

        var domain = getDomain(param.source);
        var id = '';

        if (domain.test(/taobao/ig)) {
            id = getIdFromSource(item.source, /id=(\d*)/);
        } else if (domain.test(/tmall/ig)) {
            id = getIdFromSource(item.source, /id=(\d*)/);
        } else if (domain.test(/thejamy/ig)) {
            id = getIdFromSource(item.source, /product\/[a-zA-Z0-9]*/);
        } else if (domain.test(/hm/ig)) {
            id = getIdFromSource(item.source, /page.(\d*)/);
        } else {
            id = '';
        }

        if (id == '') {
            ResponseHelper.response(res, errors.ItemNotExist);
            return;
        }

        Item.findOne({
            'sourceInfo.id': id
        }, function(error, item) {
            if(error) {
                ResponseHelper.response(res, error);
            } else if (!item) {
                ResponseHelper.response(res, errors.ItemNotExist);
            } else {
                ResponseHelper.response(res, error, {
                    item: item
                });
            }
        });
    }
};

var _getIdFromSource =  function(source, idRegex) {
    var idComp = source.match(idRegex);
    if (idComp && idComp.length > 1) {
        return idComp[1];
    } else {
        return null;
    }
};


