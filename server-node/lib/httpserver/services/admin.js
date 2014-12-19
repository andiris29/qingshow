var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');
var Show = require('../../model/shows');
var Item = require('../../model/items');

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
  var id = req.body['id'];

  if (!id || !id.length) {
    ResponseHelper.response(res, ServerError.NotEnoughParam);
    return;
  }

  People.findOne({'userInfo.id': id}, function(err, people) {
    if (err) {
      ResponseHelper.response(res, err);
      return;
    } else if (!people) {
      ResponseHelper.response(res, ServerError.PeopleNotExist);
      return;
    }

    people.remove(function(err, people) {
      ResponseHelper.response(res, err, {
        'people': people
      });
    });
  });
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
  var id = req.body["id"];
  if (!id || !id.length) {
    ResponseHelper.response(res, ServerError.NotEnoughParam);
    return;
  }
  Item.findOne({'_id': RequestHelper.parseId(id)}, function(err, item) {
    if (err) {
      ResponseHelper.response(res, err);
      return;
    } else if (!item) {
      ResponseHelper.response(res, ServerError.ItemNotExist);
      return;
    }

    item.remove(function(res, item) {
      ResponseHelper.response(res, err, {
        'item': item
      });
    });
  });
};

_saveShow = function(req, res) {
  var param = req.body;
  var cover = param.cover;
  var height = param.heigth;
  var width = param.width;

  var show = new Show({
    cover: cover,
    coverMetadata: {
      cover: cover,
      heigth: heigth,
      width: width 
    }
  });

  show.video = param.video;

  ['numLike', 'numView'].forEach(function(field) {
    if (param[field]) {
      show.set(field, parseInt(param[field]));
    }
  });
  ['poster', 'styles'].forEach(function(field) {
    if (param[field]) {
      show.set(field, ResponseHelper.parseArray(param[field]));
    }
  });

  ['modelRef', 'itemRefs', 'studioRef', 'brandRef'].forEach(function(field) {
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
  var id = req.body['id'];
  if (!id || !id.length) {
    ResponseHelper.response(res, ServerError.NotEnoughParam);
    return;
  }
  Show.findOne({'_id': id}, function(err, show) {
    if (err) {
      ResponseHelper.response(res, err);
      return;
    } else if (show) {
      ResponseHelper.response(res, ServerError.ShowNotExist);
      return;
    }
    
    show.remove(function(res, show) {
      ResponseHelper.response(res, err, {
        'show': show
      });
    });
  });
};

module.exports = {
  'savePeople' : {
    method: 'post',
    func: _savePeople,
    permissionValidators: ['loginValidator']
  },
  'removePeopleById': {
    method: 'post',
    func: _removePeopleById,
    permissionValidators: ['loginValidator']
  },
  'saveItem': {
    method: 'post',
    func: _saveItem,
    permissionValidators: ['loginValidator']
  },
  'removeItemById': {
    method: 'post',
    func: _removeItemById,
    permissionValidators: ['loginValidator']
  },
  'saveShow': {
    method: 'post',
    func: _saveShow,
    permissionValidators: ['loginValidator']
  },
  'removeShowById': {
    method: 'post',
    func: _removeShowById,
    permissionValidators: ['loginValidator']
  }
};
