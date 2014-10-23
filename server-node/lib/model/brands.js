var mongoose = require('mongoose');
var People = require('./peoples');

var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    name: String,
    logo: String,
    slogan : String,
    followRefs: { type: Schema.Types.ObjectId, ref: 'peoples'}
});
var Brand = mongoose.model('brands', brandSchema);
module.exports = Brand;