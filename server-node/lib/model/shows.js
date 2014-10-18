
var mongoose = require('mongoose');
var People = require('./peoples');
var Item = require('./items');


var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    name: String,
    cover: String,
    video: String,
    posters: [String],
    numLike: Number,
    numView: Number,
    tags: [String],
    modelRef: {type: Schema.Types.ObjectId, ref: 'peoples'},
    itemRefs: {
        type: [
            {type: Schema.Types.ObjectId, ref: 'items'}
        ],
        select: false
    }
});

//showSchema.methods.toJSON = function () {
//    return "";
//};
var Show = mongoose.model('shows', showSchema);
module.exports = Show;