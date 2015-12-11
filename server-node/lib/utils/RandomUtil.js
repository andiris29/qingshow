module.exports.random = function (min,max){
	return new Number(min+Math.random()*(max-min)).toFixed(0);
}