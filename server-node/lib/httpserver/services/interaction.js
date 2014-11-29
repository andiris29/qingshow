//model
var People = require('../../model/peoples');
var Show = require('../../model/shows');
var Comment = require('../../model/comments');
var Brand = require('../../model/brands');
var PItem = require('../../model/pItems');
var PShow = require('../../model/pShows');
//util
var mongoose = require('mongoose');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');
var nimble = require('nimble');

var _followBrand, _unfollowBrand, _like, _comment;

_followBrand = function (req, res) {
    var error = null;
    var brandIdObj = null;
    var brand = null;
    var user = null;
    nimble.parallel([
        function (parallelCallback) {
            //find brand
            nimble.series([
                function (callback) {
                    try {
                        var param = req.body;
                        var brandIdStr = param._id || "";
                        brandIdObj = mongoose.mongo.BSONPure.ObjectID(brandIdStr);
                    } catch (e) {
                        error = new ServerError(ServerError.BrandNotExist);
                    }
                    callback();

                }, function (callback) {
                    if (error) {
                        callback();
                        return;
                    }
                    Brand.findOne({_id: brandIdObj})
                        .select('followerRefs')
                        .exec(function (err, b) {
                            if (err) {
                                error = err;
                            } else if (!b) {
                                error = new ServerError(ServerError.BrandNotExist);
                            } else {
                                brand = b;
                            }
                            callback();
                        });

                }, function (callback) {
                    parallelCallback();
                    callback();
                }]);
        }, function (callback) {
            //find user
            People.findOne({_id: req.currentUser._id})
                .select("followBrandRefs")
                .exec(function (err, u) {
                    if (err) {
                        error = err;
                    } else if (!u) {
                        error = new ServerError(ServerError.NeedLogin);
                    } else {
                        user = u;
                    }
                    callback();
                });
        }
    ], function () {
        //follow
        if (error) {
            ServicesUtil.responseError(res, error);
            return;
        }
        if (user.followBrandRefs.indexOf(brand._id) === -1) {
            user.followBrandRefs.unshift(brand._id);
        } else {
            error = new ServerError(ServerError.AlreadyFollowBrand);
        }
        if (brand.followerRefs.indexOf(user._id) === -1) {
            brand.followerRefs.unshift(user._id);
        } else {
            error = new ServerError(ServerError.AlreadyFollowBrand);
        }
        nimble.parallel([
            function (callback) {
                user.save(function (err, u) {
                    if (error) {
                        callback();
                        return;
                    }
                    if (err || !u) {
                        //TODO restore
                        error = err || new Error();
                    }
                    callback();
                });
            }, function (callback) {
                brand.save(function (err, b) {
                    if (error) {
                        callback();
                        return;
                    }
                    if (err || !b) {
                        //TODO: restore
                        error = err || new Error();
                    }
                    callback();
                });
            }
        ], function () {
            if (error) {
                ServicesUtil.responseError(res, error);
            } else {
                res.send('success');
            }

        });
    });
};

_unfollowBrand = function (req, res) {
    var error = null;
    var brandIdObj = null;
    var brand = null;
    var user = null;
    nimble.parallel([
        function (parallelCallback) {
            //find brand
            nimble.series([
                function (callback) {
                    try {
                        var param = req.body;
                        var brandIdStr = param._id || "";
                        brandIdObj = mongoose.mongo.BSONPure.ObjectID(brandIdStr);
                    } catch (e) {
                        error = new ServerError(ServerError.BrandNotExist);
                    }
                    callback();

                }, function (callback) {
                    if (error) {
                        callback();
                        return;
                    }
                    Brand.findOne({_id: brandIdObj})
                        .select('followerRefs')
                        .exec(function (err, b) {
                            if (err) {
                                error = err;
                            } else if (!b) {
                                error = new ServerError(ServerError.BrandNotExist);
                            } else {
                                brand = b;
                            }
                            callback();
                        });

                }, function (callback) {
                    parallelCallback();
                    callback();
                }]);
        }, function (callback) {
            //find user
            People.findOne({_id: req.currentUser._id})
                .select("followBrandRefs")
                .exec(function (err, u) {
                    if (err) {
                        error = err;
                    } else if (!u) {
                        error = new ServerError(ServerError.NeedLogin);
                    } else {
                        user = u;
                    }
                    callback();
                });
        }
    ], function () {
        //follow
        if (error) {
            ServicesUtil.responseError(res, error);
            return;
        }
        if (user.followBrandRefs.indexOf(brand._id) !== -1) {
            user.followBrandRefs.remove(brand._id);
        } else {
            error = new ServerError(ServerError.DidNotFollowBrand);
        }
        if (brand.followerRefs.indexOf(user._id) !== -1) {
            brand.followerRefs.remove(user._id);
        } else {
            error = new ServerError(ServerError.DidNotFollowBrand);
        }

        nimble.parallel([
            function (callback) {
                user.save(function (err, u) {
                    if (err || !u) {
                        //TODO restore
                        error = err || new Error();
                    }
                    callback();
                });
            }, function (callback) {
                brand.save(function (err, b) {
                    if (err || !b) {
                        //TODO: restore
                        error = err || new ServerError(ServerError.ServerError);
                    }
                    callback();
                });
            }
        ], function () {
            if (error) {
                ServicesUtil.responseError(res, error);
            } else {
                res.send('success');
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
                        ServicesUtil.responseError(res, new ServerError.AlreadyLikeShow);
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
                                    err = err || new ServerError(ServerError.ServerError);
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
                var commentEntity = new Comment({
                    showRef: showIdObj,
                    peopleRef: userId,
                    comment : comment
                });
                commentEntity.save(function (err, c) {
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
    'followBrand' : {method: 'post', func: _followBrand, needLogin: true},
    'unfollowBrand': {method: 'post', func: _unfollowBrand, needLogin: true},
    'like' : {method: 'post', func: _like, needLogin: true},
    'comment' : {method: 'post', func: _comment, needLogin: true}
};