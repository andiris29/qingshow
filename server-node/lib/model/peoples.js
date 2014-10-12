var mongoose = require('mongoose');
var Show = require('./shows');
var People = require('./peoples');

var Schema = mongoose.Schema;
var peopleSchema;
peopleSchema = Schema({
    roles: Number,
    name: String,
    portrait: String,
    height: Number,
    weight: Number,
    gender: Number,	//male 0 female 1
    hairTypes: Number, //0 all 1 long 2 super long 3 mid long
    userInfo: {
        main: String,
        encryptedPassword: String
    },
    modelInfo: {
        status: Number,
        numberLike: Number
    },
    favoriteShowRefs: [
        { type: Schema.Types.ObjectId, ref: 'shows'}
    ],
    followerRefs: [
        { type: Schema.Types.ObjectId, ref: 'peoples'}
    ],
    followRefs: [
        { type: Schema.Types.ObjectId, ref: 'peoples'}
    ]
});
var People = mongoose.model('peoples', peopleSchema);
module.exports = People;