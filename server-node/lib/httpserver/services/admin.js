var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');
var Show = require('../../model/shows');
var Item = require('../../model/items');
var ShowChosen = require('../../model/showChosens');

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

var _savePeople, _removePeopleById, _saveItem, _removeItemById, _saveShow, _removeShowById, _saveShowChosen, _removeShowChosenById, _removeModelById, _saveModel;

_savePeople = function(req, res) {

  _saveModel(People, 'people', req, res, function(req, res) {
    var param = req.body;
    var id = param.id;
    var password = param.password;
    var people = new People({
      userInfo: {}
    });
    if (id && id.length) {
      people.userInfo['id'] = id;
    }
    if (password && password.length) {
      people.userInfo['password'] = _encrypt(password);
    }

    ['name', 'portrait', 'gender'].forEach(function(field) {
      if (param[field]) {
        people.set(field, param[field]);
      }
    });
    ['height', 'weight'].forEach(function(field) {
      if (param[field]) {
        people.set(field, parseFloat(param[field]));
      }
    });
    ['roles', 'hairTypes'].forEach(function(field) {
      if (req.body[field]) {
        people.set(field, RequestHelper.parseArray(param[field]));
      }
    });

    return people
  });
};

_removePeopleById = function(req, res) {
  _removeModelById(People, "people", req, res);
};

_saveItem = function(req, res) {
  _saveModel(Item, 'item', req, res, function(req, res){
    var param = req.body;
    var item = new Item();

    ['name', 'cover', 'source'].forEach(function(field){
      if (param[field]) {
        item.set(field, param[field]);
      }
    });

    ['brandRef'].forEach(function(field) {
      if (param[field]) {
        item.set(field, RequestHelper.parseId(param[field]));
      }
    });

    ['category'].forEach(function(field) {
      if (param[field]) {
        item.set(field, parseInt(param[field]));
      }
    });


    return item;
  });
};

_removeItemById = function(req, res) {
  _removeModelById(Item,'item', req, res);
};

_saveShow = function(req, res) {
  _saveModel(Show, 'show', req, res, function(req, res) {
    var param = req.body;
    var cover = param.cover;
    var height = param.height;
    var width = param.width;

    var show = new Show({
      cover: cover,
      coverMetadata: {
        cover: cover,
        height: height,
        width: width 
      }
    });

    show.video = param.video;

    ['numLike', 'numView', 'brandNewOrder', 'brandDiscountOrder'].forEach(function(field) {
      if (param[field]) {
        show.set(field, parseInt(param[field]));
      }
    });
    ['posters'].forEach(function(field) {
      if (param[field]) {
        show.set(field, RequestHelper.parseArray(param[field]));
      }
    });
    ['itemRefs'].forEach(function(field) {
      if (param[field]) {
        show.set(field, RequestHelper.parseIds(param[field]));
      }
    });

    ['modelRef', 'studioRef', 'brandRef'].forEach(function(field) {
      if (param[field]) {
        show.set(field, RequestHelper.parseId(param[field]));
      }
    });

    return show;
  });
};

_removeShowById = function(req, res) {
  _removeModelById(Show,'show', req, res);
};

_saveShowChosen = function(req, res) {
  _saveModel(ShowChosen, 'chosen', req, res, function(req, res) {
    var param = req.body;
    var chosen = new ShowChosen();
    ['showRefs'].forEach(function(field) {
      if (param[field]) {
        chosen.set(field, RequestHelper.parseIds(param[field]));
      }
    });

    ['type'].forEach(function(field) {
      if (param[field]) {
        chosen.set(field, parseFloat(param[field]));
      }
    });

    ['activateTime'].forEach(function(field) {
      if (param[field]) {
        chosen.set(field, Date.parse(param[field]));
      }
    });

    return chosen;
  });
}

_removeShowChosenById = function(req, res) {
  _removeModelById(ShowChosen, 'chosen', req, res);
}

_removeModelById = function(Model,name, req, res) {
  async.waterfall([
    function(callback) {
      var id = req.body['id'];
      if (!id || !id.length) {
        callback(ServerError.NotEnoughParam);
      } else {
        callback(null, id);
      }
    },
    function(id, callback) {
      Model.findOne({'_id': RequestHelper.parseId(id)}, function(err, model) {
        if (err) {
          callback(err);
        } else if (!model) {
          callback(_getNotExistError(name));
        } else {
          callback(null, model);
        }
      });
    },
    function(model, callback) {
      model.remove(function(err, model) {
        var entity = {};
        entity[name] = model;
        ResponseHelper.response(res, err, entity);
      });
    }],
    function(err) {
      ResponseHelper.response(res, err);
    }
  );
}

_saveModel = function(Model, key, req, res, parser) {
  async.waterfall([
  function(callback) {
    var model = parser(req, res);
    callback(null, model);
  },
  function(model, callback) {
    model.save(function(err, savedObject) {
      var entity = {};
      entity[key] = savedObject;
      ResponseHelper.response(res, err, entity);
    });
  }],
  function(err) {
    ResponseHelper.response(res, err);
  });
}


var _getNotExistError = function(key) {
  switch(key) {
    case 'people':
      return ServerError.PeopleNotExist;
    case 'item':
      return ServerError.ItemNotExist;
    case 'show':
      return ServerError.ShowNotExist;
    default:
      return ServerError.RequestValidationFail;
  }
}

module.exports = {
  'savePeople' : {
    method: 'post',
    func: _savePeople,
    permissionValidators: ['loginValidator', 'adminValidator']
  },
  'removePeopleById': {
    method: 'post',
    func: _removePeopleById,
    permissionValidators: ['loginValidator', 'adminValidator']
  },
  'saveItem': {
    method: 'post',
    func: _saveItem,
    permissionValidators: ['loginValidator', 'adminValidator']
  },
  'removeItemById': {
    method: 'post',
    func: _removeItemById,
    permissionValidators: ['loginValidator', 'adminValidator']
  },
  'saveShow': {
    method: 'post',
    func: _saveShow,
    permissionValidators: ['loginValidator', 'adminValidator']
  },
  'removeShowById': {
    method: 'post',
    func: _removeShowById,
    permissionValidators: ['loginValidator', 'adminValidator']
  },
  'saveShowChosen': {
    method: 'post',
    func: _saveShowChosen,
    permissionValidators: ['loginValidator', 'adminValidator']
  },
  'removeShowChosenById': {
    method: 'post',
    func: _removeShowChosenById,
    permissionValidators: ['loginValidator', 'adminValidator']
  }
};
