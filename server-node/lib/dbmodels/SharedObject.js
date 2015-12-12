var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var sharedObjectSchema = {
    create : {
        type : Date,
        'default' : Date.now
    },
    type : Number,
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
            showSnapshot: Object
        },
        trade : {
            remix : {
                master: {
                    itemSnapshot: Object,
                    rect: Object
                },
                slaves: [{
                    itemSnapshot: Object,
                    rect: Object
                }]
            }
        }
    }
}

var SharedObject = mongoose.model('sharedObjects', sharedObjectSchema);

module.exports = SharedObject;
