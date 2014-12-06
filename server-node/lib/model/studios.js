var mongoose = require('mongoose');
var People = require('./peoples');

var Studio = mongoose.model('studios', mongoose.Schema({
    name : String,
    logo : String,
    create : {
        type : Date,
        'default' : Date.now
    }
}));
module.exports = Studio;
