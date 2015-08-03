var FtpClient = require('ftp');
var winston = require('winston');
var async = require('async');

var gm = require('gm');
var imageMagick = gm.subClass({
    imageMagick : true
});
var path = require('path');
var async = require('async');
var ftpConnection;

var _connectToFtp = function(ftpConfig, reconnect, callback) {
    ftpConnection = new FtpClient();
    ftpConnection.on('ready', function() {
        winston.info('FTP connection ready');
        if (callback) {
            callback();
            callback = null;
        }

    });
    ftpConnection.on('end', function() {
        winston.info('FTP connection end');
        ftpConnection = null;
        if (reconnect) {
            _connectToFtp(ftpConfig, reconnect);
        }
    });

    ftpConnection.on('close', function() {
        winston.info('FTP connection close');
        ftpConnection = null;
        if (reconnect) {
            _connectToFtp(ftpConfig, reconnect);
        }
    });

    ftpConnection.on('error', function(err) {
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

var connect = function(ftpConfig, callback) {
    _connectToFtp(ftpConfig, true, callback);
};

var getConnection = function() {
    return ftpConnection;
};
var upload = function(input, dest, callback) {
    winston.info('ftp saving: ' + dest);
    ftpConnection.put(input, dest, function(err) {
        if (err) {
            winston.error('ftp save failed: ' + dest);
        } else {
            winston.info('ftp save success: ' + dest);
        }
        callback(err);
    });
};

var uploadWithResize = function (input, savedName, uploadPath, resizeOptions, callback) {
    var tasks = [];

    // Upload
    tasks.push(function (innerCallback) {
        upload(input, path.join(uploadPath, savedName), innerCallback);
    });

    // Resize
    (resizeOptions || []).forEach(function (option) {
        var lastDotIndex = input.lastIndexOf('.');
        var tempPre = input;
        var tempPro = "";
        if (lastDotIndex !== -1) {
            tempPre = input.substr(0, lastDotIndex);
            tempPro = input.substr(lastDotIndex);
        }
        var newPath = tempPre + option.suffix + tempPro;
        // Get target size
        if (option.rate) {
            tasks.push(function (innerCallback) {
                winston.info('imageMagick size: ' + input);
                imageMagick(input).size(function (err, size) {
                    if (err) {
                        winston.error('imageMagick size err: ' + err);
                        innerCallback(err);
                    } else {
                        winston.info('imageMagick size result: ' + size);
                        innerCallback(null, size.width * option.rate, size.height * option.rate);
                    }
                });
            });
        } else {
            tasks.push(function (innerCallback) {
                innerCallback(null, option.width, option.height);
            });
        }

        // Do resize
        tasks.push(function (width, height, innerCallback) {
            winston.info('imageMagick resize: ' + input);
            imageMagick(input).resize(width, height, '!').autoOrient().write(newPath, function (err) {
                if (err) {
                    winston.error('imageMagick resize err: ' + err);
                } else {
                    winston.info('imageMagick resize result: ' + input);
                    var savedName = path.basename(newPath);
                    var fullPath = path.join(uploadPath, savedName);
                    upload(newPath, fullPath, innerCallback);
                }
            });
        });
    });
    async.waterfall(tasks, callback);
};

module.exports = {
    'connect' : connect,
    'getConnection' : getConnection,
    'upload' : upload,
    'uploadWithResize' : uploadWithResize

};
