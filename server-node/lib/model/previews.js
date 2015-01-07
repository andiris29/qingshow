var mongoose = require('mongoose');

var previewSchema;
previewSchema = mongoose.Schema({
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

var Preview = mongoose.model('previews', previewSchema);
module.exports = Preview;
