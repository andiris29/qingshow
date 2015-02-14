var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category : Number, // <code>
    name : String,
    price: Number,
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
    brandNewInfo : {
        order : Number
    },
    brandDiscountInfo : {
        price : Number,
        order : Number
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    taobaoInfo : {
        top_num_iid : String,
        top_title : String,
        top_nick : String,
        top_type : String,
        refreshTime : Date,
        top_skus : Array,
        web_skus : Array
    }
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
