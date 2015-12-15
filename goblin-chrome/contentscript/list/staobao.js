global.siteInjection.fetchWebItems = function() {
	var webItems = [];
	$('div.item').each(function(index, dom) {
		webItems.push(_parse(dom));

		global.render.asFinding(dom);
	});

	return webItems.length ? webItems : null;
};

global.siteInjection.updateWebItemDom = function(webItem, item) {
	$('div.item').each(function(index, dom) {
		var currentWebItem = _parse(dom);
		if (currentWebItem.domain === webItem.domain && currentWebItem.id === webItem.id) {
			if (item) {
				global.render.asFound(dom);
			} else {
				global.render.asNotFound(dom);
			}
		}
	});
};

var _parse = function(dom) {
	var a$ = $('a', dom);
	return {
		'domain' : goblin.parseUrl(a$.attr('href')).domain,
		'id' : a$.attr('data-nid')
	}
};
