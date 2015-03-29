var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');

//model
var Topic = require('../../model/topics');

//util
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');

var ServerError = require('../server-error');

var topic =  module.exports;

topic.query = {
    'method' : 'get',
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            Topic.find().exec(function(error, topics) {
                callback(error, topics)
            });
        }], function(error, topics) {
            ResponseHelper.response(res, error, {
                'topics' : topics
            });
        });
    }
};

