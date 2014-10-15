var People = require('../../model/peoples');
var Show = require('../../model/shows');
var mongoose = require('mongoose');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');

var _follow, _like, _comment;
_follow = function (req, res) {
    //TODO
    res.send('follow');
};
_like = function (req, res) {
    var param = req.body;
    var showIdStr = param.showId;
    var showIdObj = mongoose.mongo.BSONPure.ObjectID(showIdStr);
    Show.findOne({_id: showIdObj}, function (err, show) {
        try {
            if (err) {
                throw err;
            } else if (!show) {
                throw new ServerError(ServerError.ShowNotExist);
            } else {
                var user = req.currentUser;
                user.favoriteShowRefs.push(showIdObj);
                user.save(function (err, p) {
                    if (err || !p) {
                        err = err || new Error();
                        throw err;
                    } else {
                        res.send('succeed');
                    }
                });
            }
        } catch (e) {
            ServicesUtil.responseError(res, e);
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