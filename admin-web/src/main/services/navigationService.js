// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * TODO Support animation
     */
    var NavigationService = function() {
        this._root = null;
        this._root$ = null;

        this._cache = {};
        this._views = [];
        this._currentView = null;
    };

    NavigationService.prototype.root = function(value) {
        this._root = value;
        this._root$ = $(this._root);
    };

    NavigationService.prototype.push = function(module) {
        if (!this._cache[module]) {
            this._load(module, function() {
                this._push(this._generateView(module));
            }.bind(this));
        } else {
            _.defer( function() {
                this._push(this._generateView(module));
            }.bind(this));
        }
    };

    NavigationService.prototype.pop = function() {

    };

    NavigationService.prototype._push = function(view) {
        if (this._currentView) {
            this._currentView.hide();
        }
        this._root$.append(view.dom());
        this._currentView = view;
    };

    NavigationService.prototype._load = function(module, callback) {
        var path = window.appConfig.moduleToPath(module);
        async.parallel([
        function(callback) {
            // Load controller class
            require([module], function(ViewClass) {
                callback(null, ViewClass);
            });
        },
        function(callback) {
            // Load dom template
            $.ajax({
                'url' : path + '.html'
            }).done(function(data) {
                callback(null, $(data));
            });
        },
        function(callback) {
            // Load css
            $.ajax({
                'url' : path + '.css'
            }).done(function(css) {
                var link$ = $(document.createElement('link'));
                link$.attr({
                    'rel' : 'stylesheet',
                    'href' : path + '.css'
                });
                link$.appendTo(document.head);
            }).always(function() {
                callback(null);
            });

        }], function(err, resuls) {
            this._cache[module] = {
                'ViewClass' : resuls[0],
                'domTemplate$' : resuls[1]
            };
            callback();
        }.bind(this));
    };

    NavigationService.prototype._generateView = function(module) {
        var cache = this._cache[module];
        return new cache.ViewClass(cache.domTemplate$.clone().get(0));
    };

    return new NavigationService();
});
