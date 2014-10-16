var People = require('../../model/peoples');
var Show = require('../../model/shows');
var Comment = require('../../model/comments');
var mongoose = require('mongoose');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');

var _follow, _like, _comment;
_follow = function (req, res) {
    //TODO refactor error handler
    //TODO handle duplicate
    try {
        var param = req.body;
        var followPeopleIdStr = param.peopleId;
        var followPeopleIdObj = mongoose.mongo.BSONPure.ObjectID(followPeopleIdStr);
        var userId = req.currentUser._id;
     } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }

    People.findOne({_id: userId})
        .select('followRefs')
        .exec(function (err, user) {
            if (err || !user) {
                err = err || new Error('err');
                ServicesUtil.responseError(res, err);
                return;
            }
            People.findOne({_id: followPeopleIdObj})
                .select('followerRefs')
                .exec(function (err, followPeople) {
                    try {
                        if (err) {
                            throw err;
                        }
                        if (!followPeople) {
                            throw new ServerError(ServerError.PeopleNotExist);
                        }
                        user.followRefs.push(followPeople._id);
                        followPeople.followerRefs.push(user._id);

                        user.save(function (err, u) {
                            try {
                                if (err || !u) {
                                    err = err || new Error;
                                    throw err;
                                }
                                followPeople.save(function (err, u) {
                                    try {
                                        if (err || !u) {
                                            err = err || new Error;
                                            throw err;
                                        }
                                    } catch (e) {
                                        //TODO Restore user.followRefs
                                        ServicesUtil.responseError(res, e);
                                    }
                                });
                                res.send('succeed');
                            } catch (e) {
                                ServicesUtil.responseError(res, e);
                                return;
                            }
                        });
                    } catch (e) {
                        ServicesUtil.responseError(res, e);
                        return;
                    }
            });
    });

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
                user.likingShowRefs.push(showIdObj);
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
    try {
        var param = req.body;
        var showIdStr = param.showId;
        var comment = param.comment;
        var showIdObj = mongoose.mongo.BSONPure.ObjectID(showIdStr);
        var userId = req.currentUser._id;

    } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }
    Show.find({_id : showIdObj})
        .select('_id')
        .exec(function (err, show) {
            if (err) {
                ServicesUtil.responseError(res, err);
            } else if (!show) {
                ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
            } else {
                var comment = new Comment({
                    showRef: showIdObj,
                    peopleRef: userId,
                    comment : comment
                });
                comment.save(function (err, c) {
                    if (err || !c) {
                        err = err || new Error();
                        ServicesUtil.responseError(res, err);
                    } else {
                        res.json(c);
                    }
                });
            }
        });
};

module.exports = {
    'follow' : {method: 'post', func: _follow, needLogin: true},
    'like' : {method: 'post', func: _like, needLogin: true},
    'comment' : {method: 'post', func: _comment, needLogin: true}
};