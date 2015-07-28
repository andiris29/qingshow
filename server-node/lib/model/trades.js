var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var tradeSchema = Schema({
    status : Number,
    totalFee : Number,
    orders : [{
        quantity : Number,
        price : Number,
        itemSnapshot : Object,
        selectedPeopleReceiverUuid : String
    }],
    pay : {
        weixin : {
            prepayid : String,
            transaction_id : String,
            partner : String,
            trade_mode : String,
            total_fee : String,
            fee_type : String,
            AppId : String,
            OpenId : String,
            time_end : String,
            notifyLogs : [{
                notify_id : String,
                trade_state : String,
                date : {
                    type : Date,
                    'default' : Date.now
                }
            }]
        },
        alipay : {
            trade_no : String,
            trade_status : String,
            total_fee : String,
            refund_status : String,
            gmt_refund : String,
            seller_id : String,
            seller_email : String,
            buyer_id : String,
            buyer_email : String,
            notifyLogs : [{
                notify_type : String,
                notify_id : String,
                trade_status : String,
                refund_status : String,
                date : {
                    type : Date,
                    'default' : Date.now
                }
            }],
        }
    },
    agent : {
        taobaoUserNick : String,
        taobaoTradeId : String
    },
    logistic : {
        company : String,
        trackingId : String
    },
    returnLogistic : {
        company : String,
        trackingId : String
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    ownerRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    statusLogs : [{
        status : Number,
        comment : String,
        update : {
            type : Date,
            'default' : Date.now
        },
        peopleRef : {
            type : Schema.Types.ObjectId,
            ref : 'peoples'
        }
    }]
});

var Trade = mongoose.model('trades', tradeSchema);
module.exports = Trade;
