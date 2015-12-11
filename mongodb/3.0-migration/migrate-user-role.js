db.getCollection('peoples').update({"mobile" : {$exists: false}}, {$set : {"role" : 0}}, false, true)
db.getCollection('peoples').update({"shopInfo" : {$ne: null}}, {$set : {"role" : 4}}, false, true)
db.getCollection('peoples').update({"nickname" : /u\d{6}/}, {$set : {role : 0}}, false, true)
