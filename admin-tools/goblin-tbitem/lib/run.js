var argv = require('minimist')(process.argv.slice(2));
var async = require('async'), _ = require('underscore');
var request = require('request');
var Iconv = require('iconv').Iconv;

// Log
var winston = require('winston'), fs = require('fs'), path = require('path');
var folderLogs = path.join(__dirname, '../logs');
if (!fs.existsSync(folderLogs)) {
    fs.mkdirSync(folderLogs);
}
winston.add(winston.transports.DailyRotateFile, {
    'filename' : path.join(folderLogs, '/winston.log')
});
// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    winston.info(new Date().toString() + ': uncaughtException');
    winston.info(err);
    winston.info('\t' + err.stack);
});

var appServerURL = argv['app-server-url'];
appServerURL = "http://" + appServerURL + "/services"

var startDate = new Date();

var _next = function() {
    var logs = [];
    async.waterfall([
    function (callback) {
        request.get({
            'url' : appServerURL + '/goblin/batchRefreshItemTaobaoInfo',
            'qs' : {
                'pageSize' : 10,
                'startDate' : startDate
            }
        }, function (err, response, body) {
            if (err) {
                callback(err);
                return;
            }
            var retObj = JSON.parse(body);
            if (retObj.metadata && retObj.metadata.err && retObj.metadata.err === 1009) {
                if (retObj.metadata.err === 1009) {
                    callback('complete');
                } else {
                    callback(retObj.metadata.err);
                }
            } else {
                callback();
            }
        })
    }], function(err) {
        if (err) {
            if (err === 'complete') {
                winston.info('all complete');
                process.exit();
            } else {
                logs.push('fail');
                winston.info(logs.join(' '));
                winston.info(err);
            }
        } else {
            logs.push('success');
            winston.info(logs.join(' '));

            // Call next after 1s, avoid to be blocked by taobao
            _.delay(function() {
                _next();
            }, 1000);
        }
    });
};
_next();
