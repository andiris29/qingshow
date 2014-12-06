var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category : String, // <code>
    cover : String,
    source : String,
    modelRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    remarkInfo : {
        sales : String
    },
    create : {
        type : Date,
        'default' : Date.now
    }
}, {
    collection : 'pItems'
});
module.exports = mongoose.model('PItem', itemSchema);
