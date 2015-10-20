function random(min,max){
	return new Number(min+Math.random()*(max-min)).toFixed(0);
}

var peoples = db.getCollection('peoples').find({});
peoples.forEach(function(people){
	if (!isNaN(people.userInfo.id)) {
		var n = parseInt(people.userInfo.id);
		if ((n >= 400 && n < 500) || (n > 600 && n < 700)) {
			if(n%10 === 1 || n%10 === 2 || n%10 === 3){
			}else {				
				var bonus = {
					'status' : NumberInt('2'),
					'money' : random(30, 300),
					'create' : new ISODate()
				}

				people.bonuses = people.bonuses || [];
				people.bonuses.push(bonus);
				db.getCollection('peoples').save(people);
			}
		}
	}
})