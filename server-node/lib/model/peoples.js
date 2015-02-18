var mongoose = require('mongoose');
var Show = require('./shows');
var Brand = require('./brands');

var Schema = mongoose.Schema;
var peopleSchema;
peopleSchema = Schema({
    __context : Object,
    roles : [Number], //user 0 model 1
    name : String,
    assetsRoot : String,
    portrait : String,
    background : String,
    height : Number,
    weight : Number,
    birthday : Date,
    gender : Number,
    hairType : Number,
    shoeSize : Number,
    clothingSize : Number,
    favoriteBrand : String,
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
    modelInfo : {
        order : Number
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
