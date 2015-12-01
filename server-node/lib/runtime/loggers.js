var winston = require('winston'),
    _ = require('underscore'),
    MongoDB = require('winston-mongodb').MongoDB;

var _winstonDbOptions = {};

var init = function() {
    var dbConfig = global.qsConfig.log.db;
    _winstonDbOptions = {
        db : 'mongodb://' + dbConfig.url + ':' + dbConfig.port + '/' + dbConfig.schema,
        username : dbConfig.user,
        password : dbConfig.password
    };
    // Default logger
    winston.add(MongoDB, _winstonDbOptions);

    // Exception logger
    new winston.Logger({
        'exceptionHandlers' : [new MongoDB(_.extend(_winstonDbOptions, {
            collection : 'uncaught-exceptions'
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
