var mongoose = require('mongoose');
var Show = require('./shows');
var Brand = require('./brands');

var Schema = mongoose.Schema;
var peopleSchema;
peopleSchema = Schema({
    __context : Object,
    roles : [Number], //user 0 model 1
    name : String,
    portrait : String,
    background : String,
    height : Number,
    weight : Number,
    birthtime : Date,
    gender : Number, //male 0 female 1
    hairTypes : [Number], //0 all 1 long 2 super long 3 mid long
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
        }
    },
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
