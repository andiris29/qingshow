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

// Build json
var interval = setInterval(function() {
	var title$ = $('h3.tb-main-title');
	if (title$.length) {
		clearInterval(interval);

		__popup(title$, {
			'name' : title$.attr('data-title').trim(),
			'source' : 'https://detail.tmall.com/item.htm?id=' + goblin.parseUrl(location.href).id
		});
	}
}, 1000);
