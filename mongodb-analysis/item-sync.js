/**
 * never/outdate/total/delist @ time
 * 474/14543/16757/--- @ 2015/11/03 20:00
 * 32/9526/16553/--- @ 2015/11/04 13:00
 * 0/0/15783/4143 @ 2015/11/05 09:00
 */

var never = db.getCollection('items').find({
    'syncEnabled' : {'$ne' : false},
    'sync' : null
}).count();

var date = new Date();
var x = date.setDate(date.getDate() - 1);
var outdate = db.getCollection('items').find({
    'syncEnabled' : {'$ne' : false},
    'sync' : {'$lt' : date}
}).count();

var total = db.getCollection('items').find({
    'syncEnabled' : {'$ne' : false}
}).count();

var delist = db.getCollection('items').find({
    'syncEnabled' : {'$ne' : false},
    'delist' : {'$ne' : null}
}).count();

print([
    'never: ' + never,
    'outdate: ' + outdate,
    'total: ' + total,
    'delist: ' + delist
]);
