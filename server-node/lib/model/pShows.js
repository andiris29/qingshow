var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    modelRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    pItemRefs : {
        type : [{
            type : Schema.Types.ObjectId,
            ref : 'pItems'
        }]
    },
}, {
    collection : 'pShows'
});
module.exports = mongoose.model('PShow', itemSchema);