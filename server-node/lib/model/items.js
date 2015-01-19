var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category : Number, // <code>
    name : String,
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
    }
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
