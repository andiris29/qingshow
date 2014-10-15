var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    name: String,
    logo: String,
    slogan : String
});