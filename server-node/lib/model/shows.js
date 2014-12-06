var mongoose = require('mongoose');
var People = require('./peoples');
var Item = require('./items');

var path = require('path');
var gm = require('gm').subClass({
    imageMagick : true
});
var http = require('http');

var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    cover : String,
    coverMetadata : {
        cover : String,
        width : Number,
        height : Number
    },
    video : String,
    posters : [String],
    numLike : Number,
    numView : Number,
    styles : [Number],
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
    brandDiscountInfo : {
        start : Date,
        end : Date
    },
    create : {
        type : Date,
        'default' : Date.now
    }
});

showSchema.methods.updateCoverMetaData = function(callback) {
    if (!this.cover.length || (this.coverMetadata && (this.cover === this.coverMetadata.cover))) {
        callback(null);
        return;
    }
    var defaultMetadata = {
        cover : "",
        width : 145,
        height : 270
    };

    gm(this.cover).size( function(err, size) {
        if (size) {
            this.coverMetadata = {
                cover : this.cover,
                width : size.width,
                height : size.height
            };
        } else {
            this.coverMetadata = defaultMetadata;
        }
        if (err) {
            callback(err);
        } else {
            this.save(function(err, image) {
                callback(err);
            });
        }
    }.bind(this));
};
var Show = mongoose.model('shows', showSchema);
module.exports = Show;
