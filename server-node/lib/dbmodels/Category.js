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
        column : Number,
        excludeDelistBefore : Date
    },
    parentRef : {
        type : Schema.Types.ObjectId,
        ref : 'categories'
    }
};

var Category = mongoose.model('categories', categorySchema);

module.exports = Category;
