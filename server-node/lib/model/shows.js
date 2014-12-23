var mongoose = require('mongoose');
var async = require('async');
var ImageUtils = require('./utils/ImageUtils');

var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    __context : Object,
    cover : String,
    coverMetadata : {
        url : String,
        width : Number,
        height : Number
    },
    horizontalCover : String,
    horizontalCoverMetadata : {
        url : String,
        width : Number,
        height : Number
    },
    video : String,
    posters : [String],
    numLike : {
        type : Number,
        'default' : 0
    },
    numView : Number,
    modelRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    itemRefs : {
        type : [{
            type : Schema.Types.ObjectId,
            ref : 'items'
        }]
        //        select: false
    },
    studioRef : {
        type : Schema.Types.ObjectId,
        ref : 'studios'
    },
    brandRef : {
        type : Schema.Types.ObjectId,
        ref : 'brands'
    },
    brandNewOrder : Number,
    brandDiscountOrder : Number,
    create : {
        type : Date,
        'default' : Date.now
    }
});

showSchema.methods.updateCoverMetaData = function(callback) {
    async.parallel([ function(callback) {
        ImageUtils.createOrUpdateMetadata(this, 'cover', callback);
    }.bind(this), function(callback) {
        ImageUtils.createOrUpdateMetadata(this, 'horizontalCover', callback);
    }.bind(this)], function(err, results) {
        callback();
    });
};

var Show = mongoose.model('shows', showSchema);
module.exports = Show;
