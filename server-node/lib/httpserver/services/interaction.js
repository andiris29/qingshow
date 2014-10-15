var People = require('../../model/peoples');
var Show = require('../../model/shows');
var ServicesUtil = require('../../util/servicesUtil');
var mongoose = require('mongoose');
var ErrorCode = require('../service-error-code');


var _follow, _like, _comment;
_follow = function (req, res) {
    //TODO
    res.send('follow');
};
_like = function (req, res) {
    var param = req.body;
    var showIdStr = param.showId;
    var showIdObj = mongoose.mongo.BSONPure.ObjectID(showIdStr);
    Show.findOne({_id : showIdObj}, function (err, show) {
        if (err) {
            ServicesUtil.responseError(res, err);
        } else if (!show) {
            err = new Error('show not exist');
            err.code = ErrorCode.ShowNotExist;
            ServicesUtil.responseError(res, err);
        } else {
            var user = req.currentUser;
            user.favoriteShowRefs.push(showIdObj);
            user.save(function (err, p) {
                if (err || !p) {
                    ServicesUtil.responseError(res, err);
                } else {
                    res.send('succeed');
                }
            });

        }
    });
};

_comment = function (req, res) {
    //TODO
    res.send('comment');
};

module.exports = {
    'follow' : {method: 'post', func: _follow, needLogin: true},
    'like' : {method: 'post', func: _like, needLogin: true},
    'comment' : {method: 'post', func: _comment, needLogin: true}
};