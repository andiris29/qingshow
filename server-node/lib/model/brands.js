var mongoose = require('mongoose');
var People = require('./peoples');

var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    name: String,
    logo: String,
    slogan : String,
    create: { type: Date, 'default': Date.now }
});
var Brand = mongoose.model('brands', brandSchema);
module.exports = Brand;