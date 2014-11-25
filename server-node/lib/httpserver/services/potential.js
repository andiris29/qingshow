var qsmail = require('../../runtime/qsmail');
var async = require('async');
//Model
var People = require('../../model/peoples');
var PItem = require('../../model/pItems');
var PShow = require('../../model/pShows');

var mongoose = require('mongoose');

//Utils
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');

var _queryAvailablePItems, _getUnshotPShows, _collocate;

// query/queryAvailablePItems [paging][get]
//
// Request
//  categories array of code
// Response
//  data.items array, entity in db.pItems

_queryAvailablePItems = function(req, res) {
    try {
        var param = req.queryString;
        var pageNo = parseInt(param.pageNo || 1);
        var pageSize = parseInt(param.pageSize || 20);
        var categories = (param.categories || '').split(',');
    } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }
    function buildQuery() {
        var query = PItem.find();
        query.where({
            'category' : {
                '$in' : categories
            },
            'collocated' : {
                '$ne' : true
            }
        });
        return query;
    }

    function additionFunc(query) {
        //TODO: add sort?
        return query;
    }

    function dateGenFunc(datas) {
        return {
            pItems : datas
        };
    }


    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, dateGenFunc, pageNo, pageSize);
};

// query/getUnshotPShows [get]
//
// Request
//  _id ObjectId in peoples
// Response
//  data.pShows array, entity in db.pShows
_getUnshotPShows = function(req, res) {
    try {
        var param = req.queryString;
        var _id = param._id;
    } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }

    PShow.find({
        'modelRef' : _id
    }, function(err, pShows) {
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        } else {
            var retObj = {
                'data' : {
                    'pShows' : pShows
                }
            };
            res.json(retObj);
            return;
        }
    });
};

_collocate = function(req, res) {
    try {
        var param = req.body;
        var _ids = param._ids.split(',');
    } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }
    var pItemRefs = ServicesUtil.stringArrayToObjectIdArray(_ids);
    var modelRef = mongoose.mongo.BSONPure.ObjectID(req.currentUser._id);
    // Find data
    var fincPItems = function(callback) {
        PItem.find({
            '_id' : {
                '$in' : pItemRefs
            },
            'collocated' : {
                '$ne' : true
            }
        }, function(err, pItems) {
            if (err) {
                callback(err);
            } else if (!pItems) {
                callback(new ServerError(ServerError.PItemNotExist));
            } else {
                callback(null, pItems);
            }
        });
    };
    var findModel = function(callback) {
        People.find({
            '_id' : modelRef
        }, function(err, models) {
            if (err) {
                callback(err);
            } else if (!models || !models.length) {
                callback(new ServerError(ServerError.PeopleNotExist));
            } else {
                callback(null, models[0]);
            }
        });
    };
    async.parallel([fincPItems, findModel], function(err, results) {
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        }
        var pItems = results[0], model = results[1];
        // Save potential show
        var savePShow = function(callback) {
            var pShow = new PShow({
                'modelRef' : mongoose.mongo.BSONPure.ObjectID(req.currentUser._id),
                'pItemRefs' : pItemRefs
            });
            pShow.save(function(err, c) {
                if (err || !c) {
                    callback(err || new Error());
                } else {
                    callback(null, c);
                }
            });
        };
        // Update potential items
        var updatePItems = function(callback) {
            pItems.forEach(function(pItem, index) {
                PItem.collection.update({
                    'source' : pItem.source
                }, {
                    '$set' : {
                        'collocated' : true
                    }
                }, {
                    'multi' : true
                }, function(err, numAffected) {
                });
            });
            callback(null);
        };
        async.parallel([savePShow, updatePItems], function(err, results) {
            var pShow = results[0];
            // Send mail
            var subject = model.get('name') + '的新搭配';
            var texts = [];
            texts.push('模特：' + model.get('name'));
            texts.push('');
            texts.push('商品：');
            texts.push(JSON.stringify(pItems, null, 4));
            qsmail.send(subject, texts.join('\n'), function(err, info) {
            });
            // Send response
            res.json(pShow);
        });
    });
};

module.exports = {
    'queryAvailablePItems' : {
        method : 'get',
        func : _queryAvailablePItems,
        needLogin : false
    },
    'getUnshotPShows' : {
        method : 'get',
        func : _getUnshotPShows,
        needLogin : false
    },
    'collocate' : {
        method : 'post',
        func : _collocate,
        needLogin : true
    }
};
