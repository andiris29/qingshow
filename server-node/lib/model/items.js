var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema = Schema({
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
    sourceInfo : {
        url : String,
        price : Number,
        description : String
    },
    brandRef : {
        type : Schema.Types.ObjectId,
        ref : 'brands'
    },
    brandNewInfo : {
        order : Number
    },
    brandDiscountInfo : {
        order : Number
    },
    create : {
        type : Date,
        'default' : Date.now
    }
});

var VersionUtil = require('../utils/VersionUtil');
itemSchema.methods.downgrade = function(to) {
    if (VersionUtils.lowerOrEqual(to, '1.2.x')) {
        if (this.sourceInfo) {
            this.source = this.sourceInfo.url;
        }
        if (this.sourceInfo && this.brandDiscountInfo) {
            this.brandDiscountInfo.price = this.price;
            this.price = this.sourceInfo.price;
        }
    }
};

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
