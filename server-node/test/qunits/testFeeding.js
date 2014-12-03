
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

