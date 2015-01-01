var newUser = testEnviroment.randomNewUser();

// Test user/register
QUnit.asyncTest("user/register", function(assert) {
  expect(4);  
  testEnviroment.request("post", "user/register", newUser, function(responseData) {
    var data = responseData.data;
    assert.ok( data != undefined, "data ok" );
    assert.ok( data.people != undefined, "people ok");
    var people = data.people;
    assert.notEqual( people.userInfo, undefined, "has userInfo");
    assert.equal( people.userInfo.id, newUser.id, "id ok");
    QUnit.start();
  });
});

// Test user/login
QUnit.asyncTest("user/login", function(assert) {
  expect(4);
  testEnviroment.request("post", "user/login", newUser, function(responseData) {
    var data = responseData.data;
    assert.ok( data != undefined, "data ok" );
    assert.ok( data.people != undefined, "people ok");
    var people = data.people;
    assert.notEqual( people.userInfo, undefined, "has userInfo");
    assert.equal( people.userInfo.id, newUser.id, "id ok");
    QUnit.start();
  })
});

// Test user/get
QUnit.asyncTest("user/get", function(assert) {
  expect(2);
  testEnviroment.request("get", "user/get", "", function(responseData) {
    var data = responseData.data;
    assert.ok( data != undefined, "data ok" );
    assert.ok( data.people != undefined, "people ok");
    var people = data.people;
    //assert.ok( people.userInfo != undefined, "has userInfo");
    //assert.equal( people.userInfo.id, newUser.id, "id ok");
    QUnit.start();
  });
});

// Test user/update
QUnit.asyncTest("user/update", function(assert) {
  expect(9);
  var updateData = {
    "currentPassword": newUser.password,
    "password": testEnviroment.randomString(10),
    "name": testEnviroment.randomString(6),
    "gender": 1,
    "height": 168,
    "weight": 47,
    "roles": 1,
    "hairTypes": "1,2,3"
  };

  testEnviroment.request("post", "user/update", updateData, function(responseData) {
    var data = responseData.data;
    assert.ok( data != undefined, "data assert" );
    assert.ok( data.people != undefined, "people assert");
    var people = data.people;
    //assert.notEqual( people.userInfo, undefined, "has userInfo");
    //assert.equal( people.userInfo.id, newUser.id, "id assert");

    assert.equal( people.name, updateData.name, "name assert");
    assert.equal( people.gender, updateData.gender, "gender assert");
    assert.equal( people.hairTypes, updateData.hairTypes, "hairTypes assert");
    assert.equal( people.height, updateData.height, "height assert");
    assert.equal( people.weight, updateData.weight, "weight assert");
    assert.equal( people.weight, updateData.weight, "weight assert");
    assert.equal( people.roles, updateData.roles, "roles assert");

    QUnit.start();
  });
});

// Test user/logout
QUnit.asyncTest("user/logout", function(assert) {
  expect(1);
  testEnviroment.request("post","user/logout", "", function(responseData) {
    assert.equal(responseData.metadata.result, 0, "result assert");
    QUnit.start();
  });
});

