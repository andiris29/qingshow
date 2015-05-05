var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var Chosen = mongoose.model('chosens', Schema({
    ref : Schema.Types.ObjectId,
    refCollection : String, //shows, items ,previews
    type : Number, // <code>
    order : Number,
    date : {
        type : Date,
        'default' : Date.now
    }
}));

module.exports = Chosen;