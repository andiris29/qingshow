global.siteInjection.fetchWebItems = function() {
	var webItems = [];
	$('div.item').each(function(index, dom) {
		var a$ = $('a', dom),
			webItem = goblin.parseUrl(a$.attr('href'));
		
		webItems.push({
			'domain' : webItem.domain,
			'id' : a$.attr('data-nid'),
			'dom' : dom
		});
	});

	return webItems.length ? webItems : null;
};
