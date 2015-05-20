var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    __context : Object,
    icon : String,
    cover : String,
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
    create : {
        type : Date,
        'default' : Date.now
    },
    recommend : {
        group: String,
        date : Date,
        description : String
    }

});

var Show = mongoose.model('shows', showSchema);
module.exports = Show;
