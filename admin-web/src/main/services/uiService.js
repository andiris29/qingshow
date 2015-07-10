// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * TODO Support animation
     */
    var UIService = function() {
        this._cache = {};
    };

    UIService.prototype.loadView = function(module, callback) {
        this._load(module, function() {
            callback(null, this._generateView(module));
        }.bind(this));
    };

    UIService.prototype.loadUi = function(module, callback) {
        this._load(module, function() {
            callback(null, this._generateUi(module));
        }.bind(this));
    };

    UIService.prototype._load = function(module, callback) {
        if (this._cache[module]) {
            _.defer( function() {
                callback(null, this._generateUi(module));
            }.bind(this));
        } else {
            async.parallel([
            function(callback) {
                // Load controller class
                require([module], function(UiClass) {
                    callback(null, UiClass);
                });
            },
            function(callback) {
                // Load dom template
                $.ajax({
                    'url' : window.appConfig.moduleToPath(module) + '.html'
                }).done(function(data) {
                    callback(null, $(data));
                });
            }], function(err, resuls) {
                var dom$ = resuls[1];
                var cache = this._cache[module] = {
                    'UiClass' : resuls[0],
                    'dom$' : dom$
                };

                var tasks = [];
                $('[module]', dom$).each( function(index, placeholder) {
                    var def = $(placeholder).attr('module').split(' as ');
                    tasks.push( function(callback) {
                        this._load(def[0], function(err, subCache) {
                            subCache.dom$.appendTo(placeholder);
                            callback(null);
                        });
                    }.bind(this));
                }.bind(this));
                async.parallel(tasks, function() {
                    callback(null, cache);
                }.bind(this));
            }.bind(this));
        }
    };

    UIService.prototype._generateView = function(module) {
        var cache = this._cache[module];
        var instance = new cache.UiClass(cache.dom$.clone().get(0), null, instance);

        this._generateSubUi(instance, instance);
        return instance;
    };

    UIService.prototype._generateUi = function(module, view) {
        var cache = this._cache[module];
        var instance = new cache.UiClass(cache.dom$.clone().get(0), null, view);

        this._generateSubUi(instance, view);
        return instance;
    };

    UIService.prototype._generateSubUi = function(instance, view) {
        $('[module]', instance.dom$()).each( function(index, placeholder) {
            var def = $(placeholder).attr('module').split(' as ');
            instance[def[1]] = this._generateUi([def[0]], view);
        }.bind(this));
    };

    return new UIService();
});
