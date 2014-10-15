var People = require('../../model/peoples');
var ServicesUtil = require('../servicesUtil');


var _models, _comments, _terms;
_models = function (req, res) {
    //TODO
    res.send('models');
};
_comments = function (req, res) {
    //TODO
    res.send('comments');
};

_terms = function (req, res) {
    //TODO
    res.send('terms');
};

module.exports = {
    'models' : {method: 'get', func: _models, needLogin: false},
    'comments' : {method: 'get', func: _comments, needLogin: false},
    'terms' : {method: 'get', func: _terms, needLogin: false}
};