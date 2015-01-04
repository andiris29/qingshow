var pageSize = testCase.shows.length;
var testFeedingHot = function(nextStep) { 

  QUnit.test("feeding/hot", function(assert) {
    var done = assert.async();
    async.waterfall([function(callback) {
      testEnviroment.request("get", "feeding/hot", {"pageSize":pageSize}, function(responseData) {
        if (responseData.metadata == undefined) {
          callback('no meta');
          return;
        }
        var metadata = responseData.metadata;
        var numTotal = metadata.numTotal;
        var numPages = metadata.numPages;
        var exceptNumPages = Math.floor(numTotal / pageSize);
        if ((numTotal % pageSize) != 0) exceptNumPages++;
        assert.equal(numPages, exceptNumPages, "page ok");
        assert.ok(responseData.data != undefined, "data assert");
        if (responseData.data == undefined) {
          callback("data do not exist");
          return;
        }
        var data = responseData.data;
        callback(null, data);
      });
    }], function(err, data) {
      if (err) {
        assert.ok(false, err);
        done();
        nextStep();
        return;
      } 
      var maxlength = testCase.shows.length - 1;
      for (var i = 0; i < testCase.shows.length; i++) {
        loopAssertShowEntity(assert, data.shows[i], testCase.shows[maxlength - i]);
      }
      done();
      nextStep();
    });
  });
}

var testFeedingByModel = function(nextStep) { 
  QUnit.test("feeding/byModel", function(assert) {
    var done = assert.async();
    async.waterfall([function(callback) {
      testEnviroment.request("get", "feeding/byModel", {"pageSize":pageSize, "_id": Setup.modelIds[0]._id}, function(responseData) {
        if (responseData.metadata == undefined) {
          callback('no meta');
          return;
        }
        var metadata = responseData.metadata;
        var numTotal = metadata.numTotal;
        var numPages = metadata.numPages;
        var exceptNumPages = Math.floor(numTotal / pageSize);
        if ((numTotal % pageSize) != 0) exceptNumPages++;
        assert.equal(numPages, exceptNumPages, "page ok");
        assert.ok(responseData.data != undefined, "data assert");
        if (responseData.data == undefined) {
          callback("data do not exist");
          return;
        }
        var data = responseData.data;
        callback(null, data);
      });
    }], function(err, data) {
      if (err) {
        assert.ok(false, err);
        done();
        nextStep();
        return;
      }

      var maxlength = 3;
      for (var i = 0; i < maxlength; i++) {
        loopAssertShowEntity(assert, data.shows[i], testCase.shows[(maxlength - i - 1) * Setup.modelIds.length]);
      }

      done();
      nextStep();
    });
  });
};

var testFeedingRecommendation = function(nextStep) { 
  QUnit.test("feeding/recommendation", function(assert) {
    var done = assert.async();
    async.waterfall([function(callback) {
      testEnviroment.request("get","feeding/recommendation", {"pageSize": pageSize}, function(responseData) {
        if (responseData.metadata == undefined) {
          callback('no meta');
          return;
        }
        var metadata = responseData.metadata;
        var numTotal = metadata.numTotal;
        var numPages = metadata.numPages;
        var exceptNumPages = Math.floor(numTotal / pageSize);
        if ((numTotal % pageSize) != 0) exceptNumPages++;
        assert.equal(numPages, exceptNumPages, "page ok");
        assert.ok(responseData.data != undefined, "data assert");
        if (responseData.data == undefined) {
          callback("data do not exist");
          return;
        }
        var data = responseData.data;
        callback(null, data);
      });
    }], function(err, data) {
      if (err) {
        assert.ok(false, err);
        done();
        nextStep();
        return;
      } 

      var maxlength = testCase.shows.length - 1;
      for (var i = 0; i < testCase.shows.length; i++) {
        loopAssertShowEntity(assert, data.shows[i], testCase.shows[maxlength - i]);
      }
      done();
      nextStep();
    });
  });
};

var testFeedingChosen = function(nextStep) {
  QUnit.test("feeding/chosen", function(assert) {
    var done = assert.async();
    async.waterfall([function(callback) {
      testEnviroment.request("get", "feeding/chosen", {"type": 1}, function(responseData) {
        if (responseData.metadata == undefined) {
          callback('no meta');
          return;
        }
        var metadata = responseData.metadata;
        var numTotal = metadata.numTotal;
        var numPages = metadata.numPages;
        var exceptNumPages = Math.floor(numTotal / pageSize);
        if ((numTotal % pageSize) != 0) exceptNumPages++;
        assert.equal(numPages, exceptNumPages, "page ok");
        assert.ok(responseData.data != undefined, "data assert");
        if (responseData.data == undefined) {
          callback("data do not exist");
          return;
        }
        var data = responseData.data;
        callback(null, data);
      });
    }], function(err, data) {
      if (err) {
        assert.ok(false, err);
        done();
        nextStep();
        return;
      } 

      for(var i = 0; i < data.shows.length; i++) {
        loopAssertShowSchema(assert, data.shows[i], Setup.showIds[i]);
      }
      done();
      nextStep();
    });
  });
};

var loopAssertShowEntity = function(assert, actual, expect) {
  assert.equal(actual.cover, expect.cover, "cover is equal");
  assert.equal(actual.coverMetadata.url, expect.coverUrl, "coverMetadata.url is equal");
  assert.equal(actual.coverMetadata.height, expect.coverHeight, "coverMetadata.height is equal");
  assert.equal(actual.coverMetadata.width, expect.coverWidth, "coverMetadata.width is equal");

  assert.equal(actual.horizontalCover, expect.horizontalCover, "horizontalCover is equal");
  assert.equal(actual.horizontalCoverMetadata.url, expect.horizontalCoverUrl, "horizontalCoverMetadata.url is equal");
  assert.equal(actual.horizontalCoverMetadata.height, expect.horizontalCoverHeight, "horizontalCoverMetadata.width is equal");
  assert.equal(actual.horizontalCoverMetadata.width, expect.horizontalCoverWidth, "horizontalCoverMetadata.width is equal");

  assert.equal(actual.video, expect.video, "video is equal");
  expect.posters.split(",").forEach(function(element, index) {
    assert.equal(actual.posters[index], element, "posters is equal");
  });
  assert.equal(actual.numLike, expect.numLike, "numLike is equal");
  assert.equal(actual.numView, expect.numView, "numView is equal");

  //assert.equal(actual.modelRef, expect.modelRef, "modelRef is equal");
  expect.itemRefs.split(",").forEach(function(element, index) {
    assert.equal(actual.itemRefs[index], element, "itemRefs is equal");
  });

  assert.equal(actual.brandRef, expect.brandRef, "brandRef is equal");
  assert.equal(actual.brandNewOrder, expect.brandNewOrder, "brandNewOrder is equal");
  assert.equal(actual.brandDiscountOrder, expect.brandDiscountOrder, "brandDiscountOrder is equal");
};

var loopAssertShowSchema = function(assert, actual, expect) {
  assert.equal(actual.cover, expect.cover, "cover is equal");
  assert.propEqual(actual.coverMetadata, expect.coverMetadata, "coverMetadata is equal");

  assert.equal(actual.horizontalCover, expect.horizontalCover, "horizontalCover is equal");
  assert.propEqual(actual.horizontalCoverMetadata, expect.horizontalCoverMetadata, "horizontalCoverMetadata is equal");

  assert.equal(actual.video, expect.video, "video is equal");
  expect.posters.forEach(function(element, index) {
    assert.equal(actual.posters[index], element, "posters is equal");
  });
  assert.equal(actual.numLike, expect.numLike, "numLike is equal");
  assert.equal(actual.numView, expect.numView, "numView is equal");

  //assert.equal(actual.modelRef, expect.modelRef, "modelRef is equal");
  expect.itemRefs.forEach(function(element, index) {
    assert.equal(actual.itemRefs[index], element, "itemRefs is equal");
  });

  assert.equal(actual.brandRef, expect.brandRef, "brandRef is equal");
  assert.equal(actual.brandNewOrder, expect.brandNewOrder, "brandNewOrder is equal");
  assert.equal(actual.brandDiscountOrder, expect.brandDiscountOrder, "brandDiscountOrder is equal");
};
