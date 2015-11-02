var winston = require('winston'),
    fs = require('fs'),
    path = require('path'),
    moment = require('moment');

var _root;

var init = function(dir) {
    _root = dir;
    _mkdir(_root);

    // Default logger
    winston.add(winston.transports.DailyRotateFile, {
        'filename' : path.join(dir, 'winston.log'),
        'timestamp' : function(){
            return moment()._d.toString();
        }
    });
    // winston.remove(winston.transports.Console);

    // Exception logger
    new winston.Logger({
        'exceptionHandlers' : [new winston.transports.DailyRotateFile({
            'filename' : path.join(dir, 'winston-exception.log'),
            'timestamp' : function(){
                return moment()._d.toString();
            }
        })],
        'exitOnError' : false
    });
};

var _registry = {};

var get = function(category) {
    if (!_registry[category]) {
        _registry[category] = true;

        _mkdir(path.join(_root, category));
        winston.loggers.add(category, {
            'transports' : [new winston.transports.DailyRotateFile({
                'filename' : path.join(_root, category, 'winston-' + category + '.log'),
                'timestamp' : function(){
                    return moment()._d.toString();
                }
            })]
        });
    }
    return winston.loggers.get(category);
};

var _mkdir = function(dir) {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir);
    }
};

module.exports = {
    'init' : init,
    'get' : get
};
