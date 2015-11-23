var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    __context : Object,
    matchComposition : String,
    categoryRef : {
        type : Schema.Types.ObjectId,
        ref : 'categories'
    },
    shopRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    expectable : {
        price : Number,
        messageForPay : String,
        messageForBuy : String,
        expired : Boolean
    },
    thumbnail : String,
    name : String,
    price: Number,
    promoPrice : Number,
    minExpectedPrice : Number,
    skuProperties : [String],
    skuTable : {},
    source : String,
    numLike : Number,
    create : {
        type : Date,
        'default' : Date.now
    },
    delist : Date,
    list : Date,
    readOnly : Boolean,
    syncEnabled : {
        type : Boolean,
        'default' : true
    },
    sync : Date
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
