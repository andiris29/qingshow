global.siteInjection.fetchWebItems = function() {
	var webItems = [],
		webItem, dom;

	// Main
	dom = $('div.tb-booth').get(0);
	if (dom) {
		webItem = goblin.parseUrl(location.href);
		webItems.push({
			'domain' : webItem.domain,
			'id' : webItem.id,
			'dom' : dom
		});
	}
	// Recommend main
	$('div.tm-combo-mitem').each(function(index, dom) {
		var a$ = $('a', dom);
		webItem = goblin.parseUrl(a$.attr('href'));
		webItems.push({
			'domain' : webItem.domain,
			'id' : webItem.id,
			'dom' : dom
		});
	});
	// Recommend
	$('div.tm-combo-item').each(function(index, dom) {
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
	var title$ = $('div.tb-detail-hd');
	if (title$.length) {
		clearInterval(interval);

		__popup(title$, {
			'name' : title$.text().trim(),
			'source' : 'https://item.taobao.com/item.htm?id=' + goblin.parseUrl(location.href).id
		});
	}
}, 1000);
