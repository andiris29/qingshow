var testCase = {}
testCase.models= [];
testCase.items = [];
testCase.shows = [];

///// 5 Models ////////
for(var i = 0; i < 5; i++) {
  testCase.models[i] = {
    'roles': 0,
    'name': 'Model ' + testEnviroment.randomString(5),
    'portrait': 'http://localhost/portrait.jpg',
    'background': 'http://localhost/background.jpg',
    'height': 162,
    'weight': 40,
    'gender': 1,
    'hairTypes': '1,2,3',
    'id': 'model_' + testEnviroment.randomString(4),
    'password': '1q2w3e4r'
  };
}
///// 60 Items ////////
for(var i = 0; i < 60; i++) {
  testCase.items[i] = {
    'category' : 0,
    'name' : 'Item_' + testEnviroment.randomString(5),
    'cover' : 'http://localhost/cover.jpg',
    'brandRef': '544280eef8c9a8acb5b19e00',
    'source' : 'http://localhost/source.jpg'
  };
}
///// 15 Shows ////////
for(var i = 0; i < 15; i++) {
  testCase.shows[i] = {
    'cover' : 'cover.jpg',
    'coverMetadata' : {
      'url' : 'http://localhost/cover.jpg',
      'width' : 300,
      'height' : 400 
    },
    'horizontalCover' : 'horizontalCover.jpg',
    'horizontalCoverMetadata' : {
      'url' : 'http://localhost/horizontalCoverMetadata.jpg',
      'width' : 640,
      'height' : 480 
    },
    'video' : 'http://localhost/video.mp4',
    'posters' : testEnviroment.randomNewUser() + ',' + testEnviroment.randomNewUser(),
    'numLike' : i,
    'numView' : i,
    // 'modelRef' : '',
    // 'itemRefs' : ''
    'studioRef' : '',
    'brandRef': '544280eef8c9a8acb5b19e00',
    'brandNewOrder' : 11,
    'brandDiscountOrder' : 12
  };
}

