var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

//model
var Item = require('../../model/items');

//util
var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper.js');

var ServerError = require('../server-error');

var item = module.exports;

item.find = {
    'method' : 'get',
    'func' : function(req, res) {
        var criteria = {};
        for (var key in req.queryString) {
            var value = req.queryString[key];
            if (!req.queryString[key] || req.queryString[key].length == 0) {
                continue;
            }
            if (key === '__context' || key === '__v') {
                continue;
            }
            if (!value || value.length == 0) {
                continue;
            }
            var column = Item.schema.paths[key];
            if (column == null) {
                continue;
            }
            var type = column.instance;

            var rawValue = value;
            if (type == 'String') {
                rawValue = value;
            } else if (type == 'Number') {
                rawValue = RequestHelper.parseNumber(value);
            } else if (type == 'Date') {
                rawValue = RequestHelper.parseDate(value);
            } else if (type == 'ObjectId') {
                rawValue = RequestHelper.parseId(value);
            } else if (type == 'Mixed') {
                continue;
            } else if (type == 'Array') {
                continue;
            }

            criteria[key] = rawValue;
        }

        Item.find(criteria, function(err, items) {
            ResponseHelper.response(res, err, {
                'items' : items
            });
        });
    }
};




