var People = require('../../model/peoples');
var ServicesUtil = require('../servicesUtil');


var _models, _comments, _terms;
_models = function (req, res) {
    param = req.body;
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;

    function buildQuery() {
        return People.find({roles : 1});
    }
    function modelDataGenFunc(data) {
        return {
            peoples: data
        };
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, null, modelDataGenFunc, pageNo, pageSize);
};
_comments = function (req, res) {
    //TODO
    res.send('comments');
};

_terms = function (req, res) {
    //TODO 暂时不做
    res.send('terms');
};

module.exports = {
    'models' : {method: 'get', func: _models, needLogin: false},
    'comments' : {method: 'get', func: _comments, needLogin: false},
    'terms' : {method: 'get', func: _terms, needLogin: false}
};