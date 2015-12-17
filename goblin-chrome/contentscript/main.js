var global = {
	'siteInjection' : {
		'fetchWebItems' : null
	}
};

var _renderAsFinding = function(dom) {
	$(dom).attr('qs-find', true).
		css('box-shadow', '0 0 12px blue');
};

var _renderAsFound = function(dom) {
	$(dom).css('box-shadow', '0 0 32px red').
		css('opacity', .3);
};

var _renderAsNotFound = function(dom) {
	$(dom).css('box-shadow', '0 0 32px green');
};

// ------------------
// chrome.runtime.onMessage
// ------------------
chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
	var response = null;
	if (request.type === 'findItemOneComplete') {
		var key = request.webItem.domain + '-' + request.webItem.id,
			webItem = _cache[key];
		
		if (request.item) {
			_renderAsFound(webItem.dom);
		} else {
			_renderAsNotFound(webItem.dom);
		}
	}
	sendResponse(response);
});
// ------------------
// Query fetch webItems
// ------------------
var _cache = {};

var interval = setInterval(function() {
	var webItems = global.siteInjection.fetchWebItems() || [],
		filteredWebItems = [];

	webItems.forEach(function(webItem) {
		var key = webItem.domain + '-' + webItem.id;

		if (!_cache[key]) {
			_cache[key] = webItem;

			_renderAsFinding(webItem.dom);
			filteredWebItems.push({
				'domain' : webItem.domain,
				'id' : webItem.id
			});
		}
	});
	if (filteredWebItems && filteredWebItems.length) {
		chrome.runtime.sendMessage({
			'type' : 'fetchWebItemsComplete', 
			'webItems' : filteredWebItems
		});
	}
}, 1000);
