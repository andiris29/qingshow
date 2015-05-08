var chosen = module.exports;
var async = require('async');
var Chosens = require('../../model/chosens');
var Items = require('../../model/items');
var ServiceHelper = require('../helpers/ServiceHelper');
var MongoHelper = require('../helpers/MongoHelper');

chosen.feed = {
    'method' : 'get',
    'func' : function (req, res) {
        ServiceHelper.queryPaging(req, res, function (qsParam, callback) {
            async.waterfall([
                function (callback) {
                    var type = qsParam.type || 0;
                    MongoHelper.queryPaging(Chosens.find({'type' : type}).sort('-date order'), Chosens.find({'type' : type}), qsParam.pageNo, qsParam.pageSize, callback);
                }, function (resultChosens, count, callback) {
                    var tasks = [];
                    resultChosens.forEach(function (resultChosen) {
                        var task = function (taskCallback) {
                            async.waterfall([
                                function (callback) {
                                    Chosens.populate(resultChosen, {
                                        'path' : 'ref',
                                        'model' : resultChosen.refCollection
                                    }, callback);

                                }, function (c, callback) {
                                    if (resultChosen.refCollection === 'items') {
                                        Items.populate(resultChosen.ref, {
                                            'path' : 'brandRef',
                                            'model' : "brands"
                                        }, callback);
                                    } else {
                                        callback();
                                    }
                                }],
                                function (err, c) {
                                    taskCallback();
                                }
                            );
                        };
                        tasks.push(task);
                    });
                    async.parallel(tasks, function (err) {
                        callback(null, resultChosens, count);
                    });
                }], callback);
        }, function (models) {
            // responseDataBuilder
            return {
                'chosens' : models
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                //update cover metadata
                MongoHelper.updateCoverMetaData(currentPageModels.map(function (m) {
                    return m.ref;
                }), callback);
            }
        });

    }
};