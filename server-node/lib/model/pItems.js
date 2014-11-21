var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category : String, // <code>
    cover : String,
    source : String,
    remarkInfo : {
        sales : String
    }
}, {
    collection : 'pItems'
});
module.exports = mongoose.model('PItem', itemSchema); 