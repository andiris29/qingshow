var mongoose = require('mongoose'),
    Schema = mongoose.Schema;
    
var bonusSchema = Schema({
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
    trigger : {
        tradeRef : {    
            type : Schema.Types.ObjectId,
            ref : 'trades'
        },
        showRefs : [{    
            type : Schema.Types.ObjectId,
            ref : 'shows'
        }]
    },
    weixinRedPack : {
        create : Date,
        send_listid : String
    }
});

bonusSchema.index({ownerRef: 1});
bonusSchema.index({create: -1});

var Bonus = mongoose.model('bonuses', bonusSchema);

module.exports = Bonus;
