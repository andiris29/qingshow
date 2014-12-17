var mongoose = require('mongoose');
var People = require('./peoples');

var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    __context : Object,
    name: String,
    logo: String,
    shopInfo : {
        address : String,
        phone : Number
    },
    create: { type: Date, 'default': Date.now }
});
var Brand = mongoose.model('brands', brandSchema);
module.exports = Brand;