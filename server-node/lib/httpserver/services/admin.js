var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');
var Show = require('../../model/shows');
var Item = require('../../model/items');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var _savePeople, _removePeopleById, _saveItem, _removeItemById, _saveShow, _removeShowById;

_savePeople = function(req, res) {
  var param, id, password;
  param = req.body;
  id = param.id;
  if (!id || !id.length) {
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
    people.save(function(err, people) {
      ResponseHelper.response(res, err, {
        'people': people
      });
    });
  });
};

_removePeopleById = function(req, res) {
  _removeModelById(People, "people", req, res);
};

_saveItem = function(req, res) {
  var param = req.body;
  if (!param) {
    ResponseHelper.response(res, ServerError.NotEnoughParam);
    return;
  }

  var item = new Item();
  item.brandRef = RequestHelper.parseId(param['brand']);
  item.category = parseInt(param['category']);
  item.name = param['name'];
  item.cover = param['cover'];
  item.source = param['source'];
  item.save(function(err, item) {
    ResponseHelper.response(res, err, {
      'item': item
    });
  });
};

_removeItemById = function(req, res) {
  _removeModelById(Item,'item', req, res);
};

_saveShow = function(req, res) {
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

  show.save(function(err, show) {
    ResponseHelper.response(res, err, {
      'show': show
    });
  });
};

_removeShowById = function(req, res) {
  _removeModelById(Show,'show', req, res);
};

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

_getNotExistError = function(key) {
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
  }
};
