var RuntimeAPI = function(crawler) {
    this._crawler = crawler;

    this._callbacks = {};
    this.queryGlobalCrawling(function(crawling) {
        if (crawling) {
            this._crawler.crawl();
        }
    });
    chrome.runtime.onMessage.addListener( function(message, sender, sendResponse) {
        if (message.method === 'crawl') {
            this._crawler.crawl();
        } else if (message.method === 'onCrawlComplete') {
            var callback = this._callbacks[message.args.crawler.id];
            if (callback) {
                callback();
            }
        }
    }.bind(this));
};

RuntimeAPI.PARALLEL_LIMIT = 1;

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

RuntimeAPI.prototype.createSubCrawers = function(urls, callback) {
    async.parallelLimit(urls.map( function(url, index) {
        return function(callback) {
            url = URI.build(_.extend(URI.parse(window.location.href), URI.parse(url)));
            chrome.runtime.sendMessage({
                'method' : 'requestCreateCrawer',
                'args' : {
                    'url' : url
                }
            }, function(crawler) {
                this._callbacks[crawler.id] = callback;
            }.bind(this));
        }.bind(this);
    }.bind(this)), RuntimeAPI.PARALLEL_LIMIT, function(err, results) {
        _complete();
    });
};

