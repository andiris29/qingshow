var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    __context : Object,
    categoryRef : {
        type : Schema.Types.ObjectId,
        ref : 'categories'
    },
    shopRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    expectable : {
        reduction : Number,
        message : String,
        expired : Boolean
    },
    remix : Boolean,
    thumbnail : String,
    name : String,
    price: Number,
    promoPrice : Number,
    skuProperties : [String],
    skuTable : {},
    source : String,
    sourceInfo : {
        // taobao, tmall, jamy, hm
        domain : String,
        id : String
    },
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
    sync : Date,
    syncRequestAt : Date,
    syncStartAt : Date,
    remixCategoryAliases : String
});

itemSchema.index({'sourceInfo.domain': 1, 'sourceInfo.id': 1});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
