var ConfigHelper = module.exports;
ConfigHelper.format = function(element){
	var result = {};
	result.slaves = [];
	for(var key in element){
		if (key === 'master') {
			result.master = {
				'rect' : JSON.parse("[" + element.master.rect + "]")
			};
		}else {
			result.slaves.push({
				'categoryRef' : element[key].categoryRef,
				'rect' : JSON.parse("[" + element[key].rect + "]")
			});
		}
	}
	return result;
}