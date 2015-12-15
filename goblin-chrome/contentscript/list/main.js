var global = {
	'siteInjection' : {
		'fetchWebItems' : null,
		'updateWebItemDom' : null
	},
	'render' : {
		'rendered' : function(dom) {
			return $(dom).attr('qs-find');
		},
		'asFinding' : function(dom) {
			$(dom).attr('qs-find', true).
				css('box-shadow', '0 0 6px blue');
		},
		'asFound' : function(dom) {
			$(dom).css('box-shadow', '0 0 12px red').
				css('opacity', .3);
		},
		'asNotFound' : function(dom) {
			$(dom).css('box-shadow', '0 0 12px green');
		}
	}
};

// ------------------
// chrome.runtime.onMessage
// ------------------
chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
	var response = null;
	if (request.type === 'findItemOneComplete') {
		global.siteInjection.updateWebItemDom(request.webItem, request.item);
	}
	sendResponse(response);
});
// ------------------
// Query fetch webItems
// ------------------

var interval = setInterval(function() {
	var webItems = global.siteInjection.fetchWebItems();
	if (webItems && webItems.length) {
		chrome.runtime.sendMessage({
			'type' : 'fetchWebItemsComplete', 
			'webItems' : webItems
		});
	}
}, 1000);
