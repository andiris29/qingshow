var Setup = {};
Setup.modelIds = [];
Setup.itemIds = [];
Setup.showIds = [];

Setup.STATE_FAILURE = 0;
Setup.STATE_WAITING = 1;
Setup.STATE_DONE = 2;

Setup.state = Setup.STATE_WAITING;

Setup.setup= function(nextStep) {
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
      testCase.shows[i].modelRef = Setup.modelIds[i % Setup.modelIds.length];
      for(var j = i * 3; j < (i * 3 + 3); j++) {
        testCase.shows[i].itemRefs.push(Setup.itemIds[j]);
      }
      testEnviroment.request("post", "admin/saveShow", testCase.shows[i], function(responseData) {
        var data = responseData.data;
        if (!data.show) {
          callback(responseData.metadata.error);
        } else {
          Setup.showIds.push(data.show._id);
          if (Setup.showIds.length == testCase.shows.length) {
            Setup.state = Setup.STATE_DONE;
            callback(null);
          }
        }
      });
    }
  }, function(callback) {
    callback(null, Setup.STATE_DONE);
  }], function(err, state) {
    if (state && state == Setup.STATE_DONE) {
      nextStep(null);
    }
  });
};

Setup.teardown= function() {
  async.waterfall([function(callback) {
    // remove model
    for (var i = 0; i < Setup.modelIds.length; i++) {
      testEnviroment.request("post","admin/removePeopleById", {"id": Setup.modelIds[i]}, function(responseData) {
        var data = responseData.data; 
        if (!data.people) {
          callback(responseData.metadata.error);
        }
      });
    }
    callback(null);
  }, 
  function(callback) {
    // remove item
    for (var i = 0; i < Setup.itemIds.length; i++) {
      testEnviroment.request("post","admin/removeItemById", {"id": Setup.itemIds[i]}, function(responseData) {
        var data = responseData.data; 
        if (!data.item) {
          callback(responseData.metadata.error);
        }
      });
    }
    callback(null);
  },
  function(callback) {
    // remove show 
    for (var i = 0; i < Setup.showIds.length; i++) {
      testEnviroment.request("post", "admin/removeShowById", {"id": Setup.showIds[i]}, function(responseData) {
        var data = responseData.data; 
        if (!data.show) {
          callback(responseData.metadata.error);
        }
      });
    }
    callback(null);
  }],
  function(err) {
  });
};

Setup.exec = function() {
  async.waterfall([function(callback) {
    Setup.setup(callback);
    //callback(null);
  },
  function(callback) {
    //callback();
    testFeedingHot(callback);
  },
  function(callback) {
    //callback();
    testFeedingByModel(callback);
  },
  function(callback) {
    //callback();
    testFeedingRecommendation(callback);
  },
  function(callback) {
    Setup.teardown();
    //callback(null);
  }],function(err) {
  });
}

Setup.exec();
