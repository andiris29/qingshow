var _togglePageAction = function() {
	_getActiveTab(function(tab) {
		if (_crawling) {
			chrome.pageAction.hide(tab.id);
		} else {
			chrome.pageAction.show(tab.id);
		}
	});
};
var _getActiveTab = function(callback) {
	_eachTab({
		'active' : true
	}, function(tab) {
		callback(tab);
	});
};
var _eachTab = function(queryInfo, iterator) {
	if (arguments.length === 1) {
		iterator = queryInfo;
		queryInfo = undefined;
	}

	queryInfo = queryInfo || {};
	async.parallel([
	function(callback) {
		chrome.tabs.query(_.extend(queryInfo, {
			'url' : 'http://*.taobao.com/*'
		}), function(tabs) {
			callback(null, tabs);
		});
	}], function(error, results) {
		_.each(results, function(tabs) {
			_.each(tabs, iterator);
		});
	});
};
/**
 * Varialbes
 */
var _crawling = false;
var _crawers = {};
/**
 * Toggle extension
 */
chrome.tabs.onCreated.addListener(function(tab) {
	_togglePageAction();
});
chrome.tabs.onUpdated.addListener(function(tab) {
	_togglePageAction();
});
chrome.tabs.onActivated.addListener(function(tab) {
	_togglePageAction();
});
/**
 *
 */
chrome.pageAction.onClicked.addListener(function(tab) {
	_crawling = true;

	_getActiveTab(function(tab) {
		chrome.tabs.sendMessage(tab.id, {
			'method' : 'crawl'
		});
	});
});

chrome.runtime.onMessage.addListener(function(message, sender, sendResponse) {
	if (message.method === 'queryCrawling') {
		sendResponse(_crawling);
	} else if (message.method === 'requestCreateCrawer') {
		chrome.tabs.create({
			'url' : message.args.url,
			'active' : false
		}, function(tab) {
			_crawers[tab.id] = sender.tab;
			sendResponse(tab);
		});
		// Return true from the event listener to indicate you wish to send a response asynchronously
		return true;
	} else if (message.method === 'crawlComplete') {
		var onwer = _crawers[sender.tab.id];
		if (onwer) {
			chrome.tabs.sendMessage(onwer.id, {
				'method' : 'onCrawlComplete',
				'args' : {
					'crawler' : sender.tab
				}
			});
			chrome.tabs.remove(sender.tab.id);
		} else {
			_crawling = false;
			sendResponse();
		}
	}

});
