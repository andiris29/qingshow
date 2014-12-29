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
    // Save Models
    for(var i = 0; i < testCase.models.length; i++) {
      testEnviroment.request("post", "admin/savePeople", testCase.models[i], function(responseData) {
        var data = responseData.data;
        if (!data.people) {
          callback(responseData.metadata.error);
        } else {
          Setup.modelIds.push(data.people._id);
          if (Setup.modelIds.length == testCase.models.length) {
            callback(null);
          }
        }
      });
    }
  }, function(callback) {
    // Save Items
    for(var i = 0; i < testCase.items.length; i++) {
      testEnviroment.request("post", "admin/saveItem", testCase.items[i], function(responseData) {
        var data = responseData.data;
        if (!data.item) {
          callback(responseData.metadata.error);
        } else {
          Setup.itemIds.push(data.item._id);
          if (Setup.itemIds.length == testCase.items.length) {
            callback(null);
          }
        }
      });
    }
  }, function(callback) {
    // Save shows
    for(var i = 0; i < testCase.shows.length; i++) {
      testEnviroment.request("post", "admin/saveShow", testCase.shows[i], function(responseData) {
        var data = responseData.data;
        if (!data.show) {
          callback(responseData.metadata.error);
        } else {
          Setup.showIds.push(data.show._id);
          if (Setup.showIds.length == testCase.shows.length) {
            Setup.state = Setup.STATE_DONE);
            callback(null);
          }
        }
      });
    }
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
