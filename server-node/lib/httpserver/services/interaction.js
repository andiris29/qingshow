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
        var followPeopleIdStr = param._id;
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
    try {
        var param = req.body;
        var showIdStr = param._id;
        var showIdObj = mongoose.mongo.BSONPure.ObjectID(showIdStr);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
    }
    Show.findOne({_id: showIdObj}, function (err, show) {
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        } else if (!show) {
            ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
            return;
        } else {
            var user = req.currentUser;
            People.findOne({_id: user._id})
                .where({likingShowRefs : showIdObj})
                .exec(function(err, tempPeople) {
                    if (err) {
                        ServicesUtil.responseError(res, err);
                        return;
                    } else if (tempPeople) {
                        ServicesUtil.responseError(res, ServerError.AlreadyLikeShow);
                        return;
                    } else {
                        People.collection.update({_id: user._id},
                            {
                                "$push": {
                                    "likingShowRefs": {
                                        "$each": [ showIdObj ],
                                        "$position": 0
                                    }
                                }
                            }, function (err, numAffected) {
                                if (err) {
                                    err = err || new Error();
                                    ServicesUtil.responseError(res, err);
                                    return;
                                } else {
                                    res.send('succeed');
                                }
                            });
                    }
                });
        }
    });
};

_comment = function (req, res) {
    try {
        var param = req.body;
        var showIdStr = param._id;
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