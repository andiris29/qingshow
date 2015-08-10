var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var promotionSchema;
promotionSchema = Schema({
    type : Number,
    criteria : Number,
    hint : String,
    description : String,
    create : {
        type : Date,
        'default' : Date.now
    }
});

var Promotion = mongoose.model('promotions', promotionSchema);
module.exports = Promotion;
