var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var sharedObjectSchema = {
    create : {
        type : Date,
        'default' : Date.now
    },
    type : Number,
    numLike : {'type' : Number, 'default' : 0},
    numDislike : {'type' : Number, 'default' : 0},
    title : String,
    description : String,
    url : String,
    icon : String,
    initiatorRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    targetInfo : {
        bonus : {
            ownerRef : {
                type : Schema.Types.ObjectId,
                ref : 'peoples'
            },
            total : Number,
            withdrawTotal : Number
        },
        show : {
            showRef : {
                type : Schema.Types.ObjectId,
                ref : 'shows'
            },
        },
        trade : {
            tradeSnapshot : Object,
            remix : {
                master: {
                    itemRef: Object,
                    rect: Object
                },
                slaves: [{
                    itemRef: Object,
                    rect: Object
                }]
            }
        }
    }
};

var SharedObject = mongoose.model('sharedObjects', sharedObjectSchema);

module.exports = SharedObject;
