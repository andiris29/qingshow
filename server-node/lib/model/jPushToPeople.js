var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var jPushToPeopleSchema = Schema({
    peopleRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    registrationId : String
});

var jPushToPeople = mongoose.model('jPushToPeople', jPushToPeopleSchema);
module.exports = jPushToPeople;
