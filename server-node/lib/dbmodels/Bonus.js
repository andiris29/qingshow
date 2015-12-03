var mongoose = require('mongoose'),
    Schema = mongoose.Schema;
    
var Bonus = mongoose.model('bonuses', Schema({
    ownerRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    type : Number,
    status : Number,
    amount : Number,
    description : String,
    icon : String,
    create : {
        type : Date,
        'default' : Date.now
    },
    participants : {
        type : [{
            type : Schema.Types.ObjectId,
            ref : 'peoples'
        }]
    },
    trigger : {
        tradeRef : {    
            type : Schema.Types.ObjectId,
            ref : 'trades'
        },
        showRef : {    
            type : Schema.Types.ObjectId,
            ref : 'shows'
        }
    },
    weixinRedPackId : String
}));

module.exports = Bonus;
