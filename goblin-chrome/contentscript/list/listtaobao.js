global.siteInjection.fetchWebItems = function() {
	var webItems = [];
	$('li.item[data-itemid]').each(function(index, dom) {
		var a$ = $('a', dom),
			webItem = goblin.parseUrl(a$.attr('href'));
		
		webItems.push({
			'domain' : webItem.domain,
			'id' : webItem.id,
			'dom' : dom
		});
	});

	return webItems.length ? webItems : null;
};
