global.siteInjection.fetchWebItems = function() {
	var webItems = [];

	// Main
	var dom = $('div.tb-gallery').get(0);
	if (dom && !global.render.rendered(dom)) {
		global.render.asFinding(dom);

		webItems.push(goblin.parseUrl(location.href));
	}
	// Recommend
	$('div.wrap').each(function(index, dom) {
		if (!global.render.rendered(dom)) {
			global.render.asFinding(dom);

			webItems.push(_parse(dom));
		}
	});

	return webItems.length ? webItems : null;
};

global.siteInjection.updateWebItemDom = function(webItem, item) {
	// Main
	var currentWebItem = goblin.parseUrl(location.href);
	if (currentWebItem.domain === webItem.domain && currentWebItem.id === webItem.id) {
		var dom = $('div.tb-gallery').get(0);
		if (item) {
			global.render.asFound(dom);
		} else {
			global.render.asNotFound(dom);
		}
	}
	// Recommend
	$('div.wrap').each(function(index, dom) {
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
