
var FtpClient = require('ftp');
var winston = require('winston');


var ftpConnection;


var _connectToFtp = function (ftpConfig, reconnect, callback) {
    ftpConnection = new FtpClient();
    ftpConnection.on('ready', function () {
        winston.info('FTP connection ready');
        if (callback) {
            callback();
            callback = null;
        }

    });
    ftpConnection.on('end', function () {
        winston.info('FTP connection end');
        ftpConnection = null;
        if (reconnect) {
            _connectToFtp(ftpConfig, reconnect);
        }
    });

    ftpConnection.on('close', function () {
        winston.info('FTP connection close');
        ftpConnection = null;
        if (reconnect) {
            _connectToFtp(ftpConfig, reconnect);
        }
    });

    ftpConnection.on('error', function (err) {
        if (callback) {
            callback(err);
            callback = null;
        }
    });


    var host = ftpConfig.internalAddress || ftpConfig.externalAddress;
    var username = ftpConfig.userName;
    var password = ftpConfig.password;
    ftpConnection.connect({
        host : host,
        user : username,
        password : password
    });

};

var connect = function (ftpConfig, callback) {
    _connectToFtp(ftpConfig, true, callback);
};

var getConnection = function () {
    return ftpConnection;
};
var upload = function (input, dest, callback) {
    ftpConnection.put(input, dest, callback);
};

module.exports = {
    'connect' : connect,
    'getConnection' : getConnection,
    'upload' : upload

};