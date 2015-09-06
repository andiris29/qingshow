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
    promoPrice : Number,
    minExpectedPrice : Number,
    skuProperties : [String],
    source : String,
    numLike : Number,
    create : {
        type : Date,
        'default' : Date.now
    },
    syncEnabled : {
        type : Boolean,
        'default' : true
    },
    sync : Date,
    delist : Date,
    readOnly : Boolean,
    returnInfo : {
        name : String,
        phone : String, 
        province : String,
        address : String
    }
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
