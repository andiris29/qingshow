var mongoose = require('mongoose');
var async = require('async');
var ImageUtils = require('./utils/ImageUtils');

var Schema = mongoose.Schema;
var previewSchema;
previewSchema = Schema({
    __context : Object,
    cover : String,
    coverMetadata : {
        url : String,
        width : Number,
        height : Number
    },
    brandDescription : String,
    nameDescription : String,
    priceDescription : String,
    numLike : {
        type : Number,
        'default' : 0
    },
    create : {
        type : Date,
        'default' : Date.now
    }
});

previewSchema.methods.updateCoverMetaData = function(callback) {
    async.parallel([ function(callback) {
        ImageUtils.createOrUpdateMetadata(this, 'cover', callback);
    }.bind(this)], function(err, results) {
        callback();
    });
};

var Preview = mongoose.model('previews', previewSchema);
module.exports = Preview;
