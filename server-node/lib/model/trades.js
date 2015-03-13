var Item = require('../../model/items');
var People = require('../../model/peoples');
var tradeSchema;
tradeSchema = Schema({
    status : Number,
    price : Number,
    orders : [{
        quantity : Number,
        price : Number,
        itemSnapshot : Item,
        peopleSnapshot : People,
        r : {
            skuIndex: Number,
            receiverIndex : Number
        }
    }],
    pay : {
        weixin : {
            appid : String,
            partnerid : String,
            prepayid : String
        },
        alipay : {
            partner : String,
            seller : String
        }
    },
    taobaoInfo : {
        userNick : String,
        tradeID : String
    },
    logistic : {
        company : String,
        trackingID : String
    },
    returnlogistic : {
        company : String,
        trackingID : String
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    statusLogs : [{
        status : Number,
        update : {
            type : Date,
            'default' : Date.now
        },
        peopleRef : {
            type : Schema.Types.ObjectId,
            ref : 'peoples';
        }
    }]
});
