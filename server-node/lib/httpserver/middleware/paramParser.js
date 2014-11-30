var ServerError = require('../server-error');

var _parsers = {};

var _init = function(services) {
    services.forEach(function(service) {
        var module = service.module, path = service.path;
        for (var id in module) {
            var parser = module[id].paramParser;
            if (parser) {
                _parsers['/services/' + path + '/' + id] = parser;
            }
        }
    });

    return _parse;
};

var _parse = function(req, res, next) {
    var parser = _parsers[req.path];
    if (parser) {
        try {
            parser(req, res, function(err, result) {
                if (err) {
                    next(new ServerError(err));
                } else {
                    req.qsParams = result;
                    next();
                }
            });
        } catch (e) {
        }
    } else {
        next();
    }
};

module.exports = _init;
