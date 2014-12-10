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
        type : {
            id : String,
            password : String,
            encryptedPassword : String,
            passwordUpdatedDate : {
                type : Date,
                'default' : Date.now
            }
        },
        select : false
    },
    modelInfo : {
        status : String,
        numberLike : Number
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