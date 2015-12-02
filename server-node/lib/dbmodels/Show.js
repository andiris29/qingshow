var mongoose = require('mongoose');
var textSearch = require('mongoose-text-search');

var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    __context : Object,
    icon : String,
    cover : String,
    coverForeground : String,
    video : String,
    posters : [String],
    numView : Number,
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
    },
    categoryNames : [String]
});

showSchema.plugin(textSearch);

showSchema.index({
    categoryNames : 'text'
}, {
    name: 'categoryNames_text_search_index',
    weights: {
        categoryNames : 1
    }
});

var Show = mongoose.model('shows', showSchema);
module.exports = Show;
