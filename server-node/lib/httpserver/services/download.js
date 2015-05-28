var mongoose = require('mongoose');
var async = require('async');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');

var ServerError = require('../server-error');
var ChannelPool = require('./ChannelPool');

var url = 'http://www.sina.com.cn';
var download = {
    'method' : 'get',
    'func' : function(req, res) {
        var clientIp = '';
        if (!req.header('X-Real-IP')) {
            clientIp = req.connection.remoteAddress;
        } else {
            clientIp = req.header('X-Real-IP');
        }
        var channel = req.queryString.channel;

        ChannelPool.pool[clientIp] = channel;

        res.redirect(url);
    }
};

module.exports = download;
