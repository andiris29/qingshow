var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category: Number,   // <code>
    cover: String,
    source: String
});
var PItem = mongoose.model('pItems', itemSchema);
module.exports = PItem;