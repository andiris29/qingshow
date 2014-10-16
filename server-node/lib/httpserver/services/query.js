var People = require('../../model/peoples');
var ServicesUtil = require('../servicesUtil');
var Comment = require('../../model/comments');
var Show = require('../../model/shows');
var ServerError = require('../server-error');
var mongoose = require('mongoose');

var _models, _comments, _terms;
_models = function (req, res) {
    var param = req.body;
    var pageNo = param.pageNo || 1;
    var pageSize = param.pageSize || 10;

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
    try {
        var param = req.body;
        var pageNo = param.pageNo || 1;
        var pageSize = param.pageSize || 10;
        var showIdStr = param.showId;
        var showIdObj = mongoose.mongo.BSONPure.ObjectID(showIdStr);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
        return;
    }
    Show.findOne({_id : showIdObj}, function (err, show) {
        function buildQuery() {
            return Comment.find({showRef : showIdObj});
        }
        function additionFunc(query) {
            query.sort({time: 1})
                .populate('peopleRef');
        }
        function commentDataGenFunc(data) {
            return {
                comments: data
            };
        }
        if (err) {
            ServicesUtil.responseError(res, err);
        } else if (!show) {
            ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
        } else {
            ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, commentDataGenFunc, pageNo, pageSize);
        }
    });
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