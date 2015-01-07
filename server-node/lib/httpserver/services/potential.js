var qsmail = require('../../runtime/qsmail');
var async = require('async');
//Model
var People = require('../../model/peoples');
var PItem = require('../../model/pItems');
var PShow = require('../../model/pShows');

var mongoose = require('mongoose');

//Utils
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

// query/queryAvailablePItems [paging][get]
//
// Request
//  categories array of code
// Response
//  data.items array, entity in db.pItems
var _queryAvailablePItems = function(req, res) {
    ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
        // querier
        var criteria = {
            'category' : {
                '$in' : qsParam.categories
            },
            'collocated' : {
                '$ne' : true
            }
        };
        MongoHelper.queryPaging(PItem.find(criteria), PItem.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
    }, function(models) {
        // responseDataBuilder
        return {
            'pItems' : models
        };
    }, {
        'afterParseRequest' : function(raw) {
            return {
                'categories' : (raw.categories || '').split(',')
            };
        }
    });
};

// query/getUnshotPShows [get]
//
// Request
//  _id ObjectId in peoples
// Response
//  data.pShows array, entity in db.pShows
var _getUnshotPShows = function(req, res) {
    try {
        var param = req.queryString;
        var _id = param._id;
    } catch (err) {
        ResponseHelper.response(res, err);
        return;
    }

    PShow.find({
        'modelRef' : _id
    }, function(err, pShows) {
        if (err) {
            ResponseHelper.response(res, err);
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

var _collocate = function(req, res) {
    var pItemRefs;
    try {
        var param = req.body;
        pItemRefs = RequestHelper.parseIds(param._ids);
    } catch (err) {
        ResponseHelper.response(res, err);
        return;
    }
    var modelRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
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
            ResponseHelper.response(res, err);
            return;
        }
        var pItems = results[0], model = results[1];
        // Save potential show
        var savePShow = function(callback) {
            var pShow = new PShow({
                'modelRef' : mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId),
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
            res.json({
                'data' : pShow
            });
        });
    });
};

module.exports = {
    'queryAvailablePItems' : {
        method : 'get',
        func : _queryAvailablePItems
    },
    'getUnshotPShows' : {
        method : 'get',
        func : _getUnshotPShows
    },
    'collocate' : {
        method : 'post',
        func : _collocate,
        permissionValidators : ['loginValidator']
    }
};
