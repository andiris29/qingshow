var FtpClient = require('ftp');
var winston = require('winston');
var async = require('async'),
    _ = require('underscore');

var gm = require('gm');
var imageMagick = gm.subClass({
    imageMagick : true
});
var path = require('path');
var async = require('async');
var fs = require('fs');
var ftpConnection;

var loggers = require('./loggers');

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
    loggers.get('performance-image-upload').info({
        'step' : 'upload',
        'dest' : dest
    });
    ftpConnection.put(input, dest, function(err) {
        if (err) {
            winston.error('ftp save failed: ' + dest);
        } else {
            loggers.get('performance-image-upload').info({
                'step' : 'upload-complete',
                'dest' : dest
            });
        }
        callback(err);
    });
};

var _insertSuffix = function(input, suffix) {
    var lastDotIndex = input.lastIndexOf('.');
    var tempPre = input;
    var tempPro = "";
    if (lastDotIndex !== -1) {
        tempPre = input.substr(0, lastDotIndex);
        tempPro = input.substr(lastDotIndex);
    }
    return tempPre + suffix + tempPro;
};

var uploadWithResize = function (input, savedName, uploadPath, resizeOptions, callback) {
    var tasks = [];

    var logRandom = _.random(100000, 999999);
    // Upload
    tasks.push(function (innerCallback) {
        upload(input, path.join(uploadPath, savedName), innerCallback);
    });

    // Resize
    (resizeOptions || []).forEach(function (option) {
        var newPath = _insertSuffix(input, option.suffix);
        // Get target size
        if (option.rate) {
            tasks.push(function (innerCallback) {
                loggers.get('performance-image-upload').info({
                    'step' : 'size',
                    'ftp.logRandom' : logRandom
                });
                imageMagick(input).size(function (err, size) {
                    if (err) {
                        winston.error('imageMagick size err: ' + err);
                        innerCallback(err);
                    } else {
                        loggers.get('performance-image-upload').info({
                            'step' : 'size-complete',
                            'ftp.logRandom' : logRandom
                        });
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
            loggers.get('performance-image-upload').info({
                'step' : 'resize',
                'ftp.logRandom' : logRandom
            });
            imageMagick(input).resize(width, height, '!').autoOrient().write(newPath, function (err) {
                if (err) {
                    winston.error('imageMagick resize err: ' + err);
                } else {
                    loggers.get('performance-image-upload').info({
                        'step' : 'resize-complete',
                        'ftp.logRandom' : logRandom
                    });
                    var n = path.basename(_insertSuffix(savedName, option.suffix));

                    upload(newPath, path.join(uploadPath, n), function (err) {
                        innerCallback (err);
                        try {
                            fs.unlink(newPath, function(){});
                        } catch(e){}
                    });
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
