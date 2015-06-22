var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    __context : Object,
    icon : String,
    cover : String,
    coverInfo : {
        backgroud : String,
        foreground : String
    },
    video : String,
    posters : [String],
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
    promotionRef : {
        type : Schema.Types.ObjectId,
        ref : 'promotions'
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    recommend : {
        group: String,
        date : Date,
        description : String
    },
    ugc : Boolean

});

var Show = mongoose.model('shows', showSchema);
module.exports = Show;
