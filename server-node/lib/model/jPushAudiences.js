var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var jPushAudiencesSchema = Schema({
    peopleRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    registrationId : String,
    create : {
        type : Date,
        'default' : Date.now
    }
});

var jPushAudiences = mongoose.model('jPushAudiences', jPushAudiencesSchema, {
    collection : 'jPushAudiences'
});
module.exports = jPushAudiences;
