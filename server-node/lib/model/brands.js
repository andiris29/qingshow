var mongoose = require('mongoose');
var People = require('./peoples');

var Schema = mongoose.Schema;
var brandSchema;
brandSchema = Schema({
    __context : Object,
    name : String,
    type : Number,
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

brandSchema.methods.updateCoverMetaData = function(callback) {
    async.parallel([ function(callback) {
        ImageUtils.createOrUpdateMetadata(this, 'cover', callback);
    }.bind(this)], function(err, results) {
        callback();
    });
};

var Brand = mongoose.model('brands', brandSchema);

module.exports = Brand;
