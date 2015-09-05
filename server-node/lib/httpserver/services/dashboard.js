var async = require('async');
var mongoose = require('mongoose');
var _ = require('underscore');

var Items = require('../../model/items');
var Trades = require('../../model/trades');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');


var dashboard = module.exports;

dashboard.itemSyncStatus = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
    }
};


dashboard.itemListingStatus = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
    }
};

dashboard.topPaidItems = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
    }
};

dashboard.topAppliedItems = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
    }
};

dashboard.tradeRevenueByDate = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
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
            },
            sort : {
                create : 1
            }
        };

        Trades.mapReduce( mapReduce, function(error, data) {
            var rows = _.map(data.results, function(element) {
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

