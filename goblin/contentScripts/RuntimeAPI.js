var RuntimeAPI = function(crawler) {
    this._crawler = crawler;

    this._callbacks = {};
    this.queryGlobalCrawling(function(crawling) {
        if (crawling) {
            this._crawler.crawl();
        }
    });
    chrome.runtime.onMessage.addListener(function(message, sender, sendResponse) {
        if (message.method === 'crawl') {
            this._crawler.crawl();
        } else if (message.method === 'onCrawlComplete') {
            var callback = _callbacks[message.args.crawler.id];
            if (callback) {
                callback();
            }
        }
    }.bind(this));
};

RuntimeAPI.prototype.complete = function() {
    chrome.runtime.sendMessage({
        'method' : 'crawlComplete'
    }, function() {
        this.queryGlobalCrawling(function(crawling) {
            if (!crawling) {
                alert('allCompleted');
            }
        });
    }.bind(this));
};

RuntimeAPI.prototype.error = function(err) {
    alert('Error, click ok and save the url.');
    this.complete();
};

RuntimeAPI.prototype.queryGlobalCrawling = function(callback) {
    chrome.runtime.sendMessage({
        'method' : 'queryCrawling'
    }, callback);
};

RuntimeAPI.prototype.createSubCrawer = function(url, callback) {
    url = URI.build(_.extend(URI.parse(window.location.href), URI.parse(url)));
    chrome.runtime.sendMessage({
        'method' : 'requestCreateCrawer',
        'args' : {
            'url' : url
        }
    }, function(crawler) {
        _callbacks[crawler.id] = callback;
    });
};

