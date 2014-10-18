var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    name: String,
    logo: String,
    slogan : String
});
var Brand = mongoose.model('brands', brandSchema);
module.exports = Brand;