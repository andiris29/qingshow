/**
 * never/outdate/total @ time
 * 474/14543/16757 @ 2015/11/03 20:00
 */
var valid = new Date();
valid.setDate(valid.getDate() - 1);

db.getCollection('items').find({
    'syncEnabled' : {
        '$ne' : false
    },
    'sync' : null
}).count();

db.getCollection('items').find({
    'syncEnabled' : {
        '$ne' : false
    },
    'sync' : {
        '$lt' : valid
    }
}).count();

db.getCollection('items').find({}).count();
