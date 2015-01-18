var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
//model
var Show = require('../../model/shows');
var ShowChosen = require('../../model/showChosens');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
//util
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');

var ServerError = require('../server-error');

var _feed = function(req, res, querier, aspectInceptions) {
    aspectInceptions = aspectInceptions || {};
    ServiceHelper.queryPaging(req, res, querier, function(models) {
        // responseDataBuilder
        return {
            'shows' : models
        };
    }, {
        'afterParseRequest' : aspectInceptions.afterParseRequest,
        'afterQuery' : function(qsParam, currentPageModels, numTotal, callback) {
            async.series([
            function(callback) {
                // Populate
                _.delay(function() {
                    Show.populate(currentPageModels.filter(function(show) {
                        show.cover += req.queryString.suffix || '';
                        return !!show;
                    }), 'modelRef', callback);
                }, (res.qsPerformance && res.qsPerformance.d) ? res.qsPerformance.d : 1);
            },
            function(callback) {
                MongoHelper.updateCoverMetaData(currentPageModels, callback);
            },
            function(callback) {
                // Append context
                ContextHelper.appendShowContext(req.qsCurrentUserId, currentPageModels, callback);
            }], callback);
        },
        'beforeEndResponse' : aspectInceptions.beforeEndResponse
    });
};

var feeding = module.exports;

feeding.recommendation = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                // TODO
            }), Show.find().limit(20), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};

feeding.hot = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numLike' : -1
            }), Show.find(), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};

feeding.like = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            async.waterfall([
            function(callback) {
                var criteria = {
                    'initiatorRef' : req.qsCurrentUserId
                };
                MongoHelper.queryPaging(RPeopleLikeShow.find(criteria).sort({
                    'create' : -1
                }).populate('targetRef'), RPeopleLikeShow.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
            },
            function(relationships, count, callback) {
                var shows = [];
                relationships.forEach(function(relationship) {
                    shows.push(relationship.targetRef);
                });
                callback(null, shows, count);
            }], callback);
        });
    }
};

feeding.chosen = {
    'method' : 'get',
    'func' : function(req, res) {
        var chosen;
        _feed(req, res, function(qsParam, callback) {
            async.waterfall([
            function(callback) {
                // Query chosen
                ShowChosen.find({
                    'type' : qsParam.type
                }).where('activateTime').lte(Date.now()).sort({
                    'activateTime' : 1
                }).limit(1).exec(function(err, chosens) {
                    if (err) {
                        callback(ServerError.fromDescription(err));
                    } else if (!chosens || !chosens.length) {
                        callback(ServerError.fromCode(ServerError.ShowNotExist));
                    } else {
                        chosen = chosens[0];
                        callback(null, chosen.showRefs.length);
                    }
                });
            },
            function(count, callback) {
                // Query shows
                var skip = (qsParam.pageNo - 1) * qsParam.pageSize;
                chosen = new ShowChosen({
                    'activateTime' : chosen.activateTime,
                    'showRefs' : chosen.showRefs.filter(function(show, index) {
                        return index >= skip && index < skip + qsParam.pageSize;
                    })
                });
                ShowChosen.populate(chosen, {
                    'path' : 'showRefs'
                }, function(err, chosen) {
                    callback(err, chosen.showRefs, count);
                });
            }], callback);
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    'type' : RequestHelper.parseNumber(raw.type) || 0
                };
            },
            'beforeEndResponse' : function(json) {
                if (chosen) {
                    json.metadata.refreshTime = chosen.activateTime;
                }
                return json;
            }
        });
    }
};

feeding.byModel = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            var criteria = {
                'modelRef' : qsParam._id
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

feeding.byBrand = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            var criteria = {
                'brandRef' : qsParam._id
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

feeding.studio = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            var criteria = {
                'studioRef' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};
