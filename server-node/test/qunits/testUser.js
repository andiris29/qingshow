var newUser = testEnviroment.randomNewUser();

// Test user/register
QUnit.asyncTest("user/register", function(assert) {
  expect(4);  
  testEnviroment.request("post", "user/register", newUser, function(responseData) {
    var data = responseData.data;
    assert.ok( data != undefined, "data ok" );
    assert.ok( data.people != undefined, "people ok");
    var people = data.people;
    assert.noEqual( people.userInfo, undefined, "has userInfo");
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
    assert.noEqual( people.userInfo, undefined, "has userInfo");
    assert.equal( people.userInfo.id, newUser.id, "id ok");
    QUnit.start();
  })
});

// Test user/get
QUnit.asyncTest("user/get", function(assert) {
  expect(4);
  testEnviroment.request("get", "user/get", "", function(responseData) {
    var data = responseData.data;
    assert.ok( data != undefined, "data ok" );
    assert.ok( data.people != undefined, "people ok");
    var people = data.people;
    assert.noEqual( people.userInfo, undefined, "has userInfo");
    assert.equal( people.userInfo.id, newUser.id, "id ok");
    QUnit.start();
  });
});

// Test user/update
