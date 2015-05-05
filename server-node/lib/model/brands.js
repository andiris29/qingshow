var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    __context : Object,
    name : String,
    logo : String,
    background : String,
    cover : String,
    coverMetadata : {
        url : String,
        width : Number,
        height : Number
    },
    shopInfo : {
        address : String,
        phone : String
    },
    create : {
        type : Date,
        'default' : Date.now
    }
});

var Brand = mongoose.model('brands', brandSchema);

module.exports = Brand;
