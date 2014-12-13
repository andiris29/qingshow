var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var crypto = require('crypto'), _secret = 'qingshow@secret';

var _encrypt = function(string) {
    var cipher = crypto.createCipher('aes192', _secret);
    var enc = cipher.update(string, 'utf8', 'hex');
    enc += cipher.final('hex');
    return enc;
};

var _decrypt = function(string) {
    var decipher = crypto.createDecipher('aes192', _secret);
    var dec = decipher.update(string, 'hex', 'utf8');
    dec += decipher.final('utf8');
    return dec;
};

var _savePeople, _removePeopleById, _saveItem, _removeItemById, _saveShow, _removeShowById;

_savePeople = function(req, res) {
  var param, id, password;
  param = req.body;
  id = param.id;
  if (!id || !id.length ) {
    ResponseHelper.response(res, ServerError.NotEnoughParam);
    return;
  }
  People.findOne({'userInfo.id': id}, function(err, people) {
    if (err) {
      ResponseHelper.response(res, err);
      return;
    } else if (people) {
      ResponseHelper.response(res, ServerError.EmailAlreadyExist);
      return;
    }

    // set people login info
    var people = new People({
      userInfo : {
        id: id 
      }
    });

    // set people attribute
    ['name', 'portrait', 'gender', 'password', 'currentPassword'].forEach(function(field) {
      if (param[field]) {
        pepole.set(field, param[field]);
      }
    });
    ['height', 'weight'].forEach(function(field) {
      if (param[field]) {
        people.set(field, parseFloat(param[field]));
      }
    });
    ['roles', 'hairTypes'].forEach(function(field) {
      if (req.body[field]) {
        people.set(field, RequestHelper,parseArray(param[field]));
      }
    });

    people.save(function(err, people) {
      ResponseHelper.response(res, err, {
        'people': people
      });
    });
  });
};


module.exports = {
  'savePeople' : {
    method: 'post',
    func: _savePeople,
    permissionValidators: ['loginValidator']
  }
};
