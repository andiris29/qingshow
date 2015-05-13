var mongoose = require('mongoose');
var Show = require('./shows');
var Brand = require('./brands');

var Schema = mongoose.Schema;
var peopleSchema;
peopleSchema = Schema({
    __context : Object,
    nickname: String,
    assetsRoot : String,
    portrait : String,
    background : String,
    height : Number,
    weight : Number,
    age : Number,
    bodyType : Number,
    dressStyle : Number,
    expectations : [Number],
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
    create : {
        type : Date,
        'default' : Date.now
    },
    update : {
        type : Date,
        'default' : Date.now
    }
});
var People = mongoose.model('peoples', peopleSchema);

module.exports = People;
