var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    __context : Object,
    icon : String,
    cover : String,
    coverForeground : String,
    video : String,
    videoPosters : [String],
    href : String,
    sticky : {'type' : Boolean, 'default' : false},
    stickyCover : String,
    numView : {
        type : Number,
        'default' : 0
    },
    numViewFirstDay : Number,
    bonusRef : {
        type : Schema.Types.ObjectId,
        ref : 'bonus'
    },
    numLike : {
        type : Number,
        'default' : 0
    },
    itemRefs : {
        type : [{
            type : Schema.Types.ObjectId,
            ref : 'items'
        }]
        //        select: false
    },
    itemReductionEnabled : {
        type : Boolean,
        'default' : true
    },
    itemRects : {
        type : Object
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    ownerRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    hideAgainstOwner : Boolean,
    recommend : {
        group: String,
        date : Date,
        description : String
    }
});

showSchema.index({ownerRef: 1});
showSchema.index({create: -1});

var Show = mongoose.model('shows', showSchema);
module.exports = Show;
