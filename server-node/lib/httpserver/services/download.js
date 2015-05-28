var mongoose = require('mongoose');
var async = require('async');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');

var ServerError = require('../server-error');

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

        require('../stores/channelStore').set(clientIp, channel);

        res.redirect('http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow');
    }
};

module.exports = download;
