var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var categorySchema = {
    name : String,
    icon : String,
    order : Number,
    matchInfo : {
        enabled : Boolean,
        defaultOnCanvas : Boolean,
        row : Number,
        column : Number
    },
    parentRef : {
        type : Schema.Types.ObjectId,
        ref : 'categories'
    },
    measureComposition : Number
};

var Category = mongoose.model('categories', categorySchema);

module.exports = Category;
