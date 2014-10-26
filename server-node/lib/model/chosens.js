var mongoose = require('mongoose');
var Show = require('./shows');
var People = require('./peoples');

var Schema = mongoose.Schema;
var chosenSchema;
chosenSchema = Schema({
    type : Number, //editor promotion
    activateTime : Date,
    showRefs : [ { type: Schema.Types.ObjectId, ref: 'shows'}]
});
var Chosen = mongoose.model('chosens', chosenSchema);

module.exports = Chosen;