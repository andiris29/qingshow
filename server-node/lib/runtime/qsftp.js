
var FtpClient = require('ftp');
var winston = require('winston');

var gm = require('gm');
var imageMagick = gm.subClass({ imageMagick : true });
var path = require('path');

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

var uploadWithResize = function (input, savedName, uploadPath, resizeOptions, callback) {
    upload(input, path.join(uploadPath, savedName), callback);

    if (resizeOptions) {
        resizeOptions.forEach(function (option) {
            var lastDotIndex = input.lastIndexOf('.');
            var tempPre = input;
            var tempPro = "";
            if (lastDotIndex != -1) {
                tempPre = input.substr(0, lastDotIndex);
                tempPro = input.substr(lastDotIndex);
            }
            var newPath = tempPre + option.suffix + tempPro;
            if (option.rate) {
                imageMagick(input)
                    .size(function (err, size) {
                        this.resize(size.width * option.rate, size.height * option.rate, '!')
                            .autoOrient()
                            .write(newPath, function (err) {
                                if (!err) {
                                    var savedName = path.basename(newPath);
                                    var fullPath = path.join(uploadPath, savedName);
                                    upload(newPath, fullPath, function (err) {});
                                }
                            });
                    });
            } else {
                imageMagick(input)
                    .resize(option.width, option.height, '!')
                    .autoOrient()
                    .write(newPath, function (err) {
                        if (!err) {
                            var savedName = path.basename(newPath);
                            var fullPath = path.join(uploadPath, savedName);
                            upload(newPath, fullPath, function (err) {});
                        }
                    });
            }
        });
    }

};


module.exports = {
    'connect' : connect,
    'getConnection' : getConnection,
    'upload' : upload,
    'uploadWithResize' : uploadWithResize


};