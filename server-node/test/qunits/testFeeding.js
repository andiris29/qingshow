var testFeeding = function() { 
  QUnit.module("Test feeding API");
  QUnit.asyncTest("feeding/recommendation", function(assert) {

    var pageSize = 7;
    expect(5);
    testEnviroment.request("get","feeding/recommendation", {"pageSize": pageSize}, function(responseData) {

      assert.ok( responseData.metadata != undefined, "metadata assert");
      if (responseData.metadata == undefined) {
        console.log("metadata do not exist");
        QUnit.start();
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
        console.log("data do not exist");
        QUnit.start();
        return;
      }
      
      var data = responseData.data;

      assert.ok(data.shows != undefined, "shows assert");
      if (data.shows == undefined) {
        QUnit.start();
        return;
      }

      assert.ok(data.shows.length > 0, "has show");
      console.log(data.shows);
      QUnit.start();
    });
  });


  QUnit.asyncTest("feeding/hot", function(assert) {

    var pageSize = 7;
    expect(5);
    testEnviroment.request("get","feeding/hot", {"pageSize": pageSize}, function(responseData) {

      assert.ok( responseData.metadata != undefined, "metadata assert");
      if (responseData.metadata == undefined) {
        console.log("metadata do not exist");
        QUnit.start();
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
        console.log("data do not exist");
        QUnit.start();
        return;
      }
      
      var data = responseData.data;

      assert.ok(data.shows != undefined, "shows assert");
      if (data.shows == undefined) {
        QUnit.start();
        return;
      }

      assert.ok(data.shows.length > 0, "has show");
      console.log(data.shows);
      QUnit.start();
    });
  });

  // Test feeding/like
  QUnit.asyncTest("feeding/like", function(assert) {
    expect(1);
    // Need login?
    testEnviroment.request("get", "feeding/like", "", function(responseData) {
      assert.ok(true, "dummy");
      QUnit.start();
    });
  });


  // Test feeding/chosen
  QUnit.asyncTest("feeding/chosen", function(assert) {
    var type=1;
    var pageSize = 7;
    expect(6)
    testEnviroment.request("get", "feeding/chosen", {"type": type, "pageSize": pageSize}, function(responseData) {
      assert.ok( responseData.metadata != undefined, "metadata assert");
      if (responseData.metadata == undefined) {
        console.log("metadata do not exist");
        QUnit.start();
        return;
      }
      var metadata = responseData.metadata;
      var numTotal = metadata.numTotal;
      var numPages = metadata.numPages;

      var exceptNumPages = Math.floor(numTotal / pageSize);

      if ((numTotal % pageSize) != 0) exceptNumPages++;

      assert.equal(numPages, exceptNumPages, "page ok");
      assert.ok(metadata.refreshTime != undefined, "refreshTime ok");
      
      assert.ok(responseData.data != undefined, "data assert");

      if (responseData.data == undefined) {
        console.log("data do not exist");
        QUnit.start();
        return;
      }
      
      var data = responseData.data;

      assert.ok(data.shows != undefined, "shows assert");
      if (data.shows == undefined) {
        QUnit.start();
        return;
      }

      assert.ok(data.shows.length > 0, "has show");
      console.log(data.shows);
      QUnit.start();
    });
  });

  // Test feeding/studio
  //QUnit.asyncTest("feeding/studio", function(assert) {
  //  assert.ok(false, "no api");
  //  QUnit.start();
  //});

  // Test feeding/byBrand
  QUnit.asyncTest("feeding/byBrand", function(assert) {
    var _id = '5472c729a006eddee471d78a';
    var pageSize = 7;
    expect(5);
    testEnviroment.request("get", "feeding/byBrand", {"_id": _id,"pageSize":pageSize}, function(responseData) {
      assert.ok( responseData.metadata != undefined, "metadata assert");
      if (responseData.metadata == undefined) {
        console.log("metadata do not exist");
        QUnit.start();
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
        console.log("data do not exist");
        QUnit.start();
        return;
      }
      
      var data = responseData.data;

      assert.ok(data.shows != undefined, "shows assert");
      if (data.shows == undefined) {
        QUnit.start();
        return;
      }

      assert.ok(data.shows.length > 0, "has show");
      console.log(data.shows);
      QUnit.start();
    });
  });
}

