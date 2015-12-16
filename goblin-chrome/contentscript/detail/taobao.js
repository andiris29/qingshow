global.siteInjection.fetchWebItems = function() {
	var webItems = [],
		webItem, dom;

	// Main
	dom = $('div.tb-gallery').get(0);
	if (dom) {
		webItem = goblin.parseUrl(location.href);
		webItems.push({
			'domain' : webItem.domain,
			'id' : webItem.id,
			'dom' : dom
		});
	}
	// Recommend
	$('div.wrap').each(function(index, dom) {
		var a$ = $('a', dom);
		webItem = goblin.parseUrl(a$.attr('href'));
		webItems.push({
			'domain' : webItem.domain,
			'id' : webItem.id,
			'dom' : dom
		});
	});

	return webItems;
};
