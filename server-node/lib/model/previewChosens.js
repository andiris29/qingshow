var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var PreviewChosen = mongoose.model('previewChosens', Schema({
    activateTime : Date,
    previewRefs : [{
        type : Schema.Types.ObjectId,
        ref : 'previews'
    }]
}, {
    collection : 'previewChosens'
}));

module.exports = PreviewChosen;
