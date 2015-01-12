var mongoose = require('mongoose');

var previewSchema;
previewSchema = mongoose.Schema({
    __context : Object,
    images : [{
        url : String,
        brandDescription : String,
        priceDescription : String,
        description : String
    }],
    imageMetadata : {
        url : String,
        width : Number,
        height : Number
    },
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
