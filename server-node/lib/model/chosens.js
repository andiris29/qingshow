var mongoose = require('mongoose');
var Show = require('./shows');
var People = require('./peoples');

var Schema = mongoose.Schema;
var chosenSchema;
chosenSchema = Schema({
    adminRef: { type: Schema.Types.ObjectId, ref: 'peoples'},
    title : String,
    description : String,
    dateStart : Date,
    showRefs : [ { type: Schema.Types.ObjectId, ref: 'shows'}]
});
var Chosen = mongoose.model('chosens', chosenSchema);

module.exports = Chosen;