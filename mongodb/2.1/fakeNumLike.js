function random(min,max){
	return new Number(min+Math.random()*(max-min)).toFixed(0);
}

var shows =  db.getCollection('shows').find({"featuredRank" : {$exists: false}});
shows.forEach(function(show){
    var fake = NumberInt(random(1, 18));
    show.numLike = fake;
    db.getCollection("shows").save(show);
})