// ------------------
// chrome.runtime.onMessage
// ------------------
chrome.runtime.onMessage.addListener(function (request, sender) {
    if (request.type === 'fetchWebItemsComplete') {
        _find(sender.tab.id, request.webItems);
    }
});

// ------------------
// Server interaction
// ------------------
var SERVER_URL = 'http://dev.chingshow.com';
// var SERVER_URL = 'http://127.0.0.1:30001';

var _find = function(tabId, webItems) {
    async.parallelLimit(webItems.map(function(webItem, index) {
        return function(callback) {
            $.ajax(SERVER_URL + '/services/item/findOneBySourceInfo', {
                'type' : 'GET',
                'dataType' : 'json',
                'data' : {
                    'domain' : webItem.domain,
                    'id' : webItem.id
                },
                'success' : function(data) {
                    chrome.tabs.sendMessage(tabId, {
                        'type' : 'findItemOneComplete', 
                        'webItem' : webItem,
                        'item' : data.data ? data.data.item : null
                    }, function(response) {callback()});
                }
            }).fail(callback);
        };
    }), 6, function(err) {
        chrome.tabs.sendMessage(tabId, {
            'type' : 'findItemComplete'
        });
    })
};
