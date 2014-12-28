console.log("Now Setup!");
var Setup = {};
Setup.modelIds = [];
Setup.itemIds = [];
Setup.showIds = [];

Setup.STATE_FAILURE = 0;
Setup.STATE_WAITING = 1;
Setup.STATE_DONE = 2;

Setup.state = Setup.STATE_WAITING;

Setup.exec = function() {
  async.waterfall([function(callback) {
    // login
    testEnviroment.request("post", "user/login", {"id": "admin", "password":"admin"}, function(responseData) {
      var data = responseData.data;
      if (!data.people) {
        callback(responseData.metadata.error);
      } else {
        callback(null);
      }
    });
  }, function(callback) {
    // Insert models
    for(var i = 0; i < testCase.models.length; i++) {
      testEnviroment.request("post", "admin/savePeople", testCase.models[i], function(responseData) {
        var data = responseData.data;
        if (!data.people) {
          callback(responseData.metadata.error);
        } else {
          Setup.modelIds.push(data.people._id);
          if (Setup.modelIds.length == testCase.models.length) {
            Setup.state = Setup.STATE_DONE;
            callback(null);
          }
        }
      });
    }
  }, function(callback) {
    callback(null);
  }, function(callback) {
    callback(null);
  }, function(callback) {
    while(Setup.state == Setup.STATE_WAITING);
    if (Setup.state == Setup.STATE_DONE) {
      testFeeding();
    }
    callback(null);
  }], function(err) {
    Setup.state = Setup.STATE_FAILURE;
  });
};

Setup.exec();
