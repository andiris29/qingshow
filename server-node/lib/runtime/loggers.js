var winston = require('winston'),
    _ = require('underscore'),
    MongoDB = require('winston-mongodb').MongoDB;

var _winstonDbOptions = {}

var init = function(logging, dbConfig) {
    _winstonDbOptions = {
        db : 'mongodb://' + dbConfig.url + ':' + dbConfig.port + '/' + logging.db.schema,
        username : dbConfig.username,
        password : dbConfig.password
    }
    // Default logger
    winston.add(MongoDB, _winstonDbOptions);

    // Exception logger
    new winston.Logger({
        'exceptionHandlers' : [new MongoDB(_.extend(_winstonDbOptions, {
            collection : 'winston-exception'
        }))],
        'exitOnError' : false
    });
};

var _registry = {};

var get = function(category) {
    if (!_registry[category]) {
        _registry[category] = true;

        winston.loggers.add(category, {
            'transports' : [new MongoDB(_.extend(_winstonDbOptions, {
                collection : category
            }))]
        });
    }
    return winston.loggers.get(category);
};

module.exports = {
    'init' : init,
    'get' : get
};
