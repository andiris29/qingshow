var SERVER_PATH = 'http://chingshow.com:30001/services/';
QUnit.config.reorder = false;

var testEnviroment = {};

testEnviroment.request = function(type, path, requestData, callback) {
  for (key in requestData) {
      if (requestData[key] instanceof Array) {
          requestData[key] = requestData[key].join(',');
      }
  }
  var request = {
    'dataType' : 'json',
    'cache' : false,
    'xhrFields' : {
        'withCredentials' : true
    },
    'url' : SERVER_PATH + path,
    'type': type,
    'data': requestData
  };

  $.ajax(request).done(function(responseData) {
    console.log('api: ' + path, requestData, responseData);
    if (callback) {
      callback(responseData);
    }
  }).fail(function(target, msg, err) {
    if (callback) {
      var metadata = {
          'error' : msg
      };
      callback(metadata);
    }
  });
}

testEnviroment.randomString = function(length) {
  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz'.split('');
  
  if (! length) {
    length = Math.floor(Math.random() * chars.length);
  }
  
  var str = '';
  for (var i = 0; i < length; i++) {
    str += chars[Math.floor(Math.random() * chars.length)];
  }
  return str;
}

testEnviroment.randomNewUser = function() {
  var userId = "qunit_" + testEnviroment.randomString(5);
  var password = testEnviroment.randomString(10);

  return {"id" : userId, "password" : password};
}
