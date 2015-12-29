var async = require('async');

var Show = require('../../dbmodels').Show,
    Trade = require('../../dbmodels').Trade,
    People = require('../../dbmodels').People,
    PeopleCode = require('../../dbmodels').PeopleCode,
    Bonus = require('../../dbmodels').Bonus,
    BonusCode = require('../../dbmodels').BonusCode,
    SharedObject = require('../../dbmodels').SharedObject,
    SharedObjectCode = require('../../dbmodels').SharedObjectCode;

var ShareHelper = require('../../helpers/ShareHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    RequestHelper = require('../../helpers/RequestHelper'),
    ServiceHelper = require('../../helpers/ServiceHelper'),
    MongoHelper = require('../../helpers/MongoHelper'),
    BonusHelper = require('../../helpers/BonusHelper');

var errors = require('../../errors');

var share = module.exports;

share.createShow = {
	'method' : 'post',
	'func' : [
        require('../middleware/validateLogin'),
        function(req, res, next) {
            ShareHelper.create(req.qsCurrentUserId, SharedObjectCode.TYPE_SHARE_SHOW, {
                'show' : {
                    'showRef': RequestHelper.parseId(req.body._id)
                }
            }, function(err, sharedObject) {
                ResponseHelper.writeData(res, {'sharedObject' : sharedObject});
                next();
            });
        }
    ]
};

share.like = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(SharedObject, '_id', 'sharedObjectRef'),
        function(req, res, next) {
            var sharedObjectRef = req.injection.sharedObjectRef;
            if (sharedObjectRef) {
                sharedObjectRef.numLike++;
                sharedObjectRef.save(function(err) {
                    ResponseHelper.writeData(res, {'sharedObject' : sharedObjectRef});
                    next();
                });
            } else {
                next(errors.NotEnoughParam);
            }
        }
    ]
};

share.dislike = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(SharedObject, '_id', 'sharedObjectRef'),
        function(req, res, next) {
            var sharedObjectRef = req.injection.sharedObjectRef;
            if (sharedObjectRef) {
                sharedObjectRef.numDislike++;
                sharedObjectRef.save(function(err) {
                    ResponseHelper.writeData(res, {'sharedObject' : sharedObjectRef});
                    next();
                });
            } else {
                next(errors.NotEnoughParam);
            }
        }
    ]
};

share.query = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res,function(qsParam, callback){
            var criteria = {};
            if (qsParam._ids && qsParam._ids.length > 0) {
                criteria._id = {
                    '$in' : RequestHelper.parseIds(qsParam._ids)
                };
                MongoHelper.queryPaging(SharedObject.find(criteria), SharedObject.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
            } else {
                callback(errors.NotEnoughParam);
            }
        },function(sharedObjects){
            return {'sharedObjects': sharedObjects};
        });
    }
};

