var mongoose = require('mongoose');
var People = require('./peoples');

var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    type: Number, //brand studio
    name: String,
    logo: String,
    slogan : String,
    followerRefs: {
        type : [
            { type: Schema.Types.ObjectId, ref: 'peoples'}
        ],
        select: false
    }
});
var Brand = mongoose.model('brands', brandSchema);
module.exports = Brand;