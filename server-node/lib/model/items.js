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
    source : String,
    numLike : Number,
    selectedSkuId : String,
    create : {
        type : Date,
        'default' : Date.now
    },
    taobaoInfo : Object
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
