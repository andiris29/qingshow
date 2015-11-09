var mongoose = require('mongoose');
var textSearch = require('mongoose-text-search');
var async = require('async');

var Item = require('./Item');
var Category = require('./Category');


var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    __context : Object,
    icon : String,
    cover : String,
    coverForeground : String,
    video : String,
    featuredRank : {
        type : Number,
        'default' : 0
    },
    posters : [String],
    numView : Number,
    numLike : {
        type : Number,
        'default' : 0
    },
    itemRefs : {
        type : [{
            type : Schema.Types.ObjectId,
            ref : 'items'
        }]
        //        select: false
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    ownerRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    hideAgainstOwner : Boolean,
    recommend : {
        group: String,
        date : Date,
        description : String
    },
    categoryNames : [String]
});

showSchema.post('save', function (doc) {
    if (!doc.categoryNames || !doc.categoryNames.length) {
        var itemIds = doc.itemRefs || [];
        async.waterfall([
            function (callback) {
                Item.find({
                    '_id' : {
                        '$in' : itemIds
                    }
                }, callback);
            }, function (items, callback) {
                items = items || [];
                var categoryIds = items.map(function (i) {
                    return i.categoryRef;
                }).filter(function (c) {
                    return !!c;
                });
                Category.find({
                    '_id' : {
                        '$in' : categoryIds
                    }
                }, callback);
            }, function (categories, callback) {
                var categoriyNames = categories.map(function (c) {
                    return c.name;
                });
                categoriyNames = categoriyNames.filter(function (n, index) {
                    return categoriyNames.indexOf(n) === index;
                });
                doc.categoryNames = categoriyNames;
                if (categoriyNames.length) {
                    doc.save(callback);
                }
            }
        ], function (err) {
            console.log(err);
        });
    }
});


showSchema.plugin(textSearch);

showSchema.index({
    categoryNames : 'text'
}, {
    name: 'categoryNames_text_search_index',
    weights: {
        categoryNames : 1
    }
});




var Show = mongoose.model('shows', showSchema);
module.exports = Show;
