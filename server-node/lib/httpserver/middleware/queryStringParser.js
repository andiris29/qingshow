var qs = require('qs');
var parseurl = require('parseurl');

function parser(req, res, next) {
    if (req.method === 'get' || req.method === 'GET') {
        var query = qs.parse(parseurl(req).query, {
            'arrayLimit' : 0
        });
        req.queryString = JSON.parse(JSON.stringify(query));
    }
    if (!global.qsUser) {
        global.qsUser = (req.queryString && req.queryString.qsUser === 'qsUser');
    }
    if (!global.qsUser) {
        next();
    }
}

module.exports = parser;
