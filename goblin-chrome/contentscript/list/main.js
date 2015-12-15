var global = {
	'siteInjection' : {
		'fetchWebItems' : null,
		'updateWebItemDom' : null
	},
	'render' : {
		'asFinding' : function(dom) {
			$(dom).css('-webkit-filter', 'blur(3px)');
		},
		'asFound' : function(dom) {
			$(dom).css('-webkit-filter', '').
				css('box-shadow', '0 0 12px red');
		},
		'asNotFound' : function(dom) {
			$(dom).css('-webkit-filter', '').
				css('box-shadow', '0 0 12px green');
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
	if (webItems) {
		clearInterval(interval);

		chrome.runtime.sendMessage({
			'type' : 'fetchWebItemsComplete', 
			'webItems' : webItems
		});
	}
}, 100);
