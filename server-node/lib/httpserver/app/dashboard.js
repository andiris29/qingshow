var async = require('async');
var mongoose = require('mongoose');
var _ = require('underscore');

var Items = require('../../dbmodels').Item;
var Trades = require('../../dbmodels').Trade;

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');


var dashboard = module.exports;

dashboard.itemSyncStatus = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var mapReduce = {
            map : function() {
                var now = new Date();
                now.setDate(now.getDate() - 1);
                if (this.syncEnabled === false) {
                    emit('disabled', 1);
                }
                if (this.sync === null || this.sync === undefined) {
                    emit('notSynced', 1);
                } else if (this.sync >= now) {
                    emit('normal', 1);
                } else {
                    emit('outdated', 1);
                }
            },
            reduce : function(key, values) {
                return Array.sum(values);
            },
            query : {},
            out : {
                inline : 1
            }
        };

        Items.mapReduce(mapReduce, function(error, results) {
            if (error) {
                ResponseHelper.response(res, error);
                return;
            }
            var returnData = {
                disabled : 0,
                notSynced : 0,
                normal : 0,
                outdated : 0
            };

            results.forEach(function(e) {
                returnData[e._id] = e.value;
            });

            ResponseHelper.response(res, error, {
                disabled : returnData.disabled,
                notSynced : returnData.notSynced,
                normal : returnData.normal,
                outdated : returnData.outdated
            });
        });
    }
};


dashboard.itemListingStatus = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var mapReduce = {
            map : function() {
                if (this.delist) {
                    emit('delist', 1);
                }
                if (!this.list) {
                    emit('notListed', 1);
                } else {
                    if (!this.delist) {
                        emit('normal', 1);
                        if (this.readOnly === true) {
                            emit('readOnly', 1);
                        }
                    }
                }
            },
            reduce : function(key, values) {
                return Array.sum(values);
            },
            query : {},
            out : {
                inline : 1
            }
        };

        Items.mapReduce(mapReduce, function(error, results) {
            if (error) {
                ResponseHelper.response(res, error);
                return;
            }
            var returnData = {
                delist: 0,
                notListed: 0,
                normal : 0,
                readOnly: 0
            };

            results.forEach(function(e) {
                returnData[e._id] = e.value;
            });

            ResponseHelper.response(res, error, {
                delist: returnData.delist,
                notListed: returnData.notListed,
                normal : returnData.normal,
                readOnly: returnData.readOnly
            });
        });
    }
};

dashboard.topPaidItems = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var mapReduce = {
            map : function() {
                emit(this.itemRef, {
                    name: this.itemSnapshot.name, 
                    quantity : this.quantity,
                    revenue : (this.totalFee === undefined ? 0 : this.totalFee)
                });
            },
            reduce : function(key, values) {
                var newValue = {
                    name : '',
                    quantity : 0,
                    revenue : 0
                };
                if (values instanceof Array) {
                    values.forEach(function(e) {
                        newValue.name = e.name;
                        newValue.quantity += e.quantity;
                        newValue.revenue += e.revenue;
                    });
                    return newValue;
                }
            },
            query : {
                status : {
                    '$nin' : [0, 1]
                }
            },
            out : {
                inline : 1
            } 
        };
        Trades.mapReduce(mapReduce, function(error, results) {
            if (error) {
                ResponseHelper.response(res, error);
                return;
            }
            var lists = _.map(results, function(e) {
                return e;
            });
            lists.sort(function(n, m) {
                if (req.queryString.sort === "quantity") {
                    return m.value.quantity - n.value.quantity;
                } else {
                    return m.value.revenue - n.value.revenue;
                }
            });
            var limits = _.first(lists, req.queryString.n);
            var rows = _.map(limits, function(e) {
                return {
                    _id : e._id,
                    name : e.value.name,
                    quantity : e.value.quantity,
                    revenue : e.value.revenue
                };
            });

            ResponseHelper.response(res, error, {
                rows : rows 
            });
        });
    }
};

dashboard.topAppliedItems = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var mapReduce = {
            map : function() {
                emit(this.itemRef, {
                    name: this.itemSnapshot.name, 
                    quantity : this.quantity,
                    revenue : (this.totalFee === undefined ? 0 : this.totalFee)
                });
            },
            reduce : function(key, values) {
                var newValue = {
                    name : '',
                    quantity : 0,
                    revenue : 0
                };
                if (values instanceof Array) {
                    values.forEach(function(e) {
                        newValue.name = e.name;
                        newValue.quantity += e.quantity;
                        newValue.revenue += e.revenue;
                    });
                    return newValue;
                }
            },
            query : {
                status : 0
            },
            out : {
                inline : 1
            }
        };

        Trades.mapReduce(mapReduce, function(error, results) {
            if (error) {
                ResponseHelper.response(res, error);
                return;
            }
            results.sort(function(n, m) {
                if (req.queryString.sort === "quantity") {
                    return m.value.quantity - n.value.quantity;
                } else {
                    return m.value.revenue - n.value.revenue;
                }
            });
            var limits = _.first(results, req.queryString.n);
            var rows = _.map(limits, function(e) {
                return {
                    _id : e._id,
                    name : e.value.name,
                    quantity : e.value.quantity,
                    revenue : e.value.revenue
                };
            });

            ResponseHelper.response(res, error, {
                rows : rows 
            });
        });
    }
};

dashboard.tradeRevenueByDate = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var mapReduce = {
            map : function() {
                var targetLogs = [];
                
                this.statusLogs.forEach(function(element) {
                    if (element.status === 2) {
                        targetLogs.push(element);
                    }
                });
                if (targetLogs.length > 0) {
                    var year = this.update.getFullYear();
                    var month = this.update.getMonth() + 1;
                    var day = this.update.getDate();
                    month = ("0" + month).substr(-2);
                    day = ("0" + day).substr(-2);
                    emit(year + '-' + month + '-' + day, this.totalFee);
                }
            },
            reduce : function(key, values) {
                return Array.sum(values);
            },
            query : {
                status : {
                    '$nin' : [0, 1]
                }
            },
            out : {
                inline : 1
            }
        };

        Trades.mapReduce(mapReduce, function(error, results) {
            var rows = _.map(results, function(element) {
                return {
                    date : element._id,
                    revenue : element.value
                };
            });
            ResponseHelper.response(res, error, {
                rows : rows
            });
        });
    }
};

dashboard.tradeNumCreatedyDate = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var mapReduce = {
            map : function() { 
                var year = this.create.getFullYear();
                var month = this.create.getMonth() + 1;
                var day = this.create.getDate();
                month = ("0" + month).substr(-2);
                day = ("0" + day).substr(-2);
                emit(year + '-' + month + '-' + day, 1);
            },
            reduce : function(key, values) {
                return Array.sum(values);
            },
            query : {},
            out : {
                inline : 1
            }
        };

        Trades.mapReduce( mapReduce, function(error, results) {
            var rows = _.map(results, function(element) {
                return {
                    date : element._id,
                    count : element.value
                };
            });
            ResponseHelper.response(res, error, {
                rows : rows
            });
        });
    }
};

