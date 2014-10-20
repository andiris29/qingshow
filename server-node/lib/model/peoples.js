var mongoose = require('mongoose');
var Show = require('./shows');
var People = require('./peoples');

var Schema = mongoose.Schema;
var peopleSchema;
peopleSchema = Schema({
    roles: [Number],  //user 0 model 1
    name: String,
    portrait: String,
    height: Number,
    weight: Number,
    gender: Number, //male 0 female 1
    hairTypes: Number, //0 all 1 long 2 super long 3 mid long
    userInfo: {
        type: {
            mail: String,
            encryptedPassword: String,
            passwordUpdatedDate: { type: Date, default: Date.now }
        },
        select: false
    },
    modelInfo: {
        status: String,
        numberLike: Number
    },
    likingShowRefs: {
        type: [
            { type: Schema.Types.ObjectId, ref: 'shows'}
        ],
        select: false
    },
    followerRefs: {
        type: [
            { type: Schema.Types.ObjectId, ref: 'peoples'}
        ],
        select: false
    },
    followRefs: {
        type: [
            { type: Schema.Types.ObjectId, ref: 'peoples'}
        ],
        select: false
    }
});
var People = mongoose.model('peoples', peopleSchema);

module.exports = People;