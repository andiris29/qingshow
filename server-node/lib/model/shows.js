var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    __context : Object,
    icon : String,
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
    brandRef : {
        type : Schema.Types.ObjectId,
        ref : 'brands'
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    recommend : {
        group: String,
        date : {
            type : Date,
            'default' : Date.now
        },
        description : String
    }

});

var Show = mongoose.model('shows', showSchema);
module.exports = Show;
