var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var Topic = mongoose.model('topic', Schema({
    cover : String,
    horizontalCover : String,
    title : String,
    subTitle : String, 
    showRefs : [{
        type : Schema.Types.ObjectId,
        ref : 'shows'
    }],
    create : {
        type : Date,
        'default' : Date.now
    },
    active : Boolean
}, {
    collection : 'topics'
}));

module.exports = Topic;
