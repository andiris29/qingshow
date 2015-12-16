global.siteInjection.fetchWebItems = function() {
	var webItems = [];
	$('div.product[data-id]').each(function(index, dom) {
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
