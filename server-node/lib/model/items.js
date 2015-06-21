var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    __context : Object,
    categoryRef : {
        type : Schema.Types.ObjectId,
        ref : 'categories'
    },
    thumbnail : String,
    name : String,
    price: Number,
    deactive : Boolean,
    images : [{
        url : String,
        description : String
    }],
    source : String,
    numLike : Number,
    create : {
        type : Date,
        'default' : Date.now
    },
    taobaoInfo : Object
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
