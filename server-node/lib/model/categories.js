var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var categorySchema = {
    name : String,
    icon : String,
    order : Number,
    matchInfo : {
        enabled : Boolean,
        defaultOnCavase : Boolean,
        row : Number,
        column : Number
    },
    parentRef : {
        type : Schema.Types.ObjectId,
        ref : 'categories'
    }
};

var Category = mongoose.model('categories', categorySchema);

module.exports = Category;
