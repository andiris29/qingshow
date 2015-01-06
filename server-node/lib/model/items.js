var mongoose = require('mongoose');
var async = require('async');
var ImageUtils = require('./utils/ImageUtils');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category : Number, // <code>
    name : String,
    cover : String,
    brandRef : {
        type : Schema.Types.ObjectId,
        ref : 'brands'
    },
    source : String,
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
