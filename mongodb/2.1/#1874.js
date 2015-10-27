//查询发布过佣金的账号数量
var f = db.getCollection('trades').find({'create' : {'$lt' : new ISODate("2015-10-25T15:18:48.735Z") , '$gt' : new ISODate("2015-10-19")},'status' : 2, 'pay.forge' : {$exists : true}}).sort({create : -1})
var hash = {};
var res = f.map(function(elem){
    var propRef = elem.promoterRef
    if(!hash[propRef]){
        hash[propRef] = true;
        return propRef;
        }
    }).filter(function(elem){
        return elem;
        });
print(res.length)

//查询新增发布show的用户数量
var f = db.getCollection('peoples').find({'create' : {'$lt' : new ISODate("2015-10-25T15:18:48.735Z") , '$gt' : new ISODate("2015-10-19")}});
var r = [];
f.forEach(function(people){
    if(db.getCollection('shows').find({'ownerRef' : people._id}).size() > 0){
        r.push(people);
        }
    })
print(r.length)
