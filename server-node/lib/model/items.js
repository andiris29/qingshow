var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category : Number, // <code>
    name : String,
    cover : String,
    coverMetadata : {
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

itemSchema.methods.updateCoverMetaData = function(callback) {
    async.parallel([ function(callback) {
        ImageUtils.createOrUpdateMetadata(this, 'cover', callback);
    }.bind(this)], function(err, results) {
        callback();
    });
};

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
