var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    __context : Object,
    category : Number, // <code>
    name : String,
    price: Number,
    deactive : Boolean,
    images : [{
        url : String,
        description : String
    }],
    imageMetadata : {
        url : String,
        width : Number,
        height : Number
    },
    source : String,
    brandRef : {
        type : Schema.Types.ObjectId,
        ref : 'brands'
    },
    numLike : Number,
    create : {
        type : Date,
        'default' : Date.now
    },
    taobaoInfo : Object
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
