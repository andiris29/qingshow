var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var ShowChosen = mongoose.model('showChosens', Schema({
    type : Number, //editor promotion
    activateTime : Date,
    showRefs : [{
        type : Schema.Types.ObjectId,
        ref : 'shows'
    }]
}, {
    collection : 'showChosens'
}));

module.exports = ShowChosen;
