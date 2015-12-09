var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var peopleSchema;
peopleSchema = Schema({
    __context : Object,
    nickname: String,
    mobile: String,
    portrait : String,
    background : String,
    height : Number,
    weight : Number,
    age : Number,
    bodyType : Number,
    dressStyle : Number,
    expectations : [Number],
    role : {
        type : Number,
        'default' : 0
    },
    initiatorRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    measureInfo : {
        shoulder : Number,
        bust : Number,
        waist : Number,
        hips : Number,
        shoeSize : Number
    },
    userInfo : {
        id : String,
        password : {
            type : String,
            select : false
        },
        encryptedPassword : {
            type : String,
            select : false
        },
        passwordUpdatedDate : {
            type : Date,
            select : false,
            'default' : Date.now
        },
        weixin : {
            openid : String,
            nickname : String,
            sec : Number,
            province : String,
            country : String,
            headimgurl : String,
            unionid : String
        },
        weibo : {
            id : String,
            screen_name : String,
            province : Number,
            country : Number,
            gender : String,
            avatar_large : String
        }
    },
    receivers : [{
        uuid: String,
        name : String,
        phone : String,
        province : String,
        address : String,
        isDefault : Boolean
    }],
    unreadNotifications : [{
        create : {
            type : Date,
            'default' : Date.now
        },
        extra : Object
    }],
    create : {
        type : Date,
        'default' : Date.now
    },
    update : {
        type : Date,
        'default' : Date.now
    },
    questSharing : {
        status : Number,
        progress : Number,
        reward : {
            id : String,
            receiverUuid : String
        }
    },
    shopInfo : {
        taobao : {
            sid : String
        }
    },
    rank : Number
});

peopleSchema.index({mobile: 1});
peopleSchema.index({create: -1});

var People = mongoose.model('peoples', peopleSchema);

module.exports = People;
