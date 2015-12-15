global.siteInjection.fetchWebItems = function() {
	var webItems = [];
	$('div.product[data-id]').each(function(index, dom) {
		if (!global.render.rendered(dom)) {
			global.render.asFinding(dom);

			webItems.push(_parse(dom));
		}
	});

	return webItems.length ? webItems : null;
};

global.siteInjection.updateWebItemDom = function(webItem, item) {
	$('div.product[data-id]').each(function(index, dom) {
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
	return goblin.parseUrl(a$.attr('href'));
};
