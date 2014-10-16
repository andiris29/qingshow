( {
    optimize : 'none',
    baseUrl : '../src/js',
    paths : {
        'ui' : './ui',
        'app' : './app'
    },
    include : ['app/views/producer/P02Model', 
        'app/views/show/S01Home', 
        'app/views/show/S02Feeding', 
        'app/views/show/S03Show', 
        'app/views/user/U01User', 
        'app/views/user/U02UserSetting'],
    name : 'app'
})
