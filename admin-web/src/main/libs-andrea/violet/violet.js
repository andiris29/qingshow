(function() {
    var violet = window.violet = {};
    // ------------------
    // violet.oo
    // ------------------
    var _extend = function(subClz, superClz) {
        var subClzPrototype = subClz.prototype;
        // Add the superclass prototype to the subclass definition
        subClz.superclass = superClz.prototype;
        // Copy prototype
        var F = function() {
        };
        F.prototype = superClz.prototype;
        subClz.prototype = new F();
        for (var prop in subClzPrototype) {
            if (subClzPrototype.hasOwnProperty(prop)) {
                subClz.prototype[prop] = subClzPrototype[prop];
            }
        }
        subClz.prototype.constructor = subClz;
        if (superClz.prototype.constructor == Object.prototype.constructor) {
            superClz.prototype.constructor = superClz;
        }
        return subClz;
    };

    violet.oo = {
        'extend' : _extend
    };
    // ------------------
    // violet.string
    // ------------------
    var _substitute = function(str, rest) {
        if (!str) {
            return '';
        }
        for (var i = 1; i < arguments.length; i++) {
            str = str.replace(new RegExp("\\{" + (i - 1) + "\\}", "g"), arguments[i]);
        }
        return str;
    };

    violet.string = {
        'substitute' : _substitute
    };
    // ------------------
    // violet.ui
    // ------------------
    var factory = (function() {
        var uiFactory = {};

        var _config = {
            'moduleToControllerClass' : function(module) {
                return module;
            },
            'moduleToTemplatePath' : function(module) {
                return module;
            }
        };
        var _cache = {};

        uiFactory.config = function(value) {
            _config = value;
        };

        uiFactory.createView = function(module, initOptions, callback) {
            uiFactory.createUi(module, initOptions, null, callback);
        };

        uiFactory.createUi = function(module, initOptions, view, callback) {
            _load(module, function() {
                callback(null, _generateUi(module, initOptions, view));
            });
        };

        var _load = function(module, callback) {
            if (_cache[module]) {
                _.defer(function() {
                    callback(null);
                });
            } else {
                async.parallel([
                function(callback) {
                    // Load controller class
                    require([_config.moduleToControllerClass(module)], function(UiClass) {
                        callback(null, UiClass);
                    });
                },
                function(callback) {
                    // Load dom template
                    $.ajax({
                        'url' : _config.moduleToTemplatePath(module)
                    }).done(function(data) {
                        callback(null, $(data));
                    });
                }], function(err, resuls) {
                    var dom$ = resuls[1];
                    var cache = _cache[module] = {
                        'UiClass' : resuls[0],
                        'dom$' : dom$
                    };

                    var tasks = [];
                    $('[module]', dom$).each(function(index, placeholder) {
                        var def = $(placeholder).attr('module').split(' as ');
                        tasks.push(function(callback) {
                            _load(def[0], function(err) {
                                var subCache = _cache[def[0]];
                                subCache.dom$.appendTo(placeholder);
                                callback(null);
                            });
                        });
                    });
                    async.parallel(tasks, function() {
                        callback(null);
                    });
                });
            }
        };

        var _generateUi = function(module, initOptions, view) {
            var cache = _cache[module];
            var ui = new cache.UiClass(cache.dom$.clone().get(0), initOptions, view);

            _generateSubModule(ui, ui.ownerView());

            return ui;
        };

        var _generateSubModule = function(ui, view) {
            $('[module]', ui.dom$()).each(function(index, placeholder) {
                var def = $(placeholder).attr('module').split(' as ');
                ui[def[1]] = _generateUi([def[0]], null, view);
            });
        };

        return uiFactory;
    })();

    var UIBase = function(dom, initOptions, ownerView) {
        if (dom.get) {
            this._dom = dom.get(0);
            this._dom$ = dom;
        } else {
            this._dom = dom;
            this._dom$ = $(dom);
        }
        this._initOptions = initOptions;

        this._ownerView = ownerView;

        this._trigger$ = $({});
        this.$ = function(selector) {
            return $(selector, this._dom);
        };
        this._destroyed = false;
    };

    UIBase.prototype.dom$ = function() {
        return this._dom$;
    };

    UIBase.prototype.dom = function() {
        return this._dom;
    };

    UIBase.prototype.hide = function() {
        this._dom$.hide();
    };

    UIBase.prototype.show = function() {
        this._dom$.show();
    };
    // ------ Lifecyle ------
    UIBase.prototype.ownerView = function() {
        return this._ownerView;
    };
    UIBase.prototype.destroy = function() {
        this.trigger('destroying');

        this._dom$.empty();
        this.$ = null;
        this._destroyed = true;

        this.trigger('destroy');
        this.off();
    };

    // ------ Event ------
    UIBase.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    UIBase.prototype.off = function() {
        this._trigger$.off.apply(this._trigger$, arguments);
    };

    UIBase.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };

    // ------ Measure ------
    UIBase.prototype.getPreferredSize = function() {
    };

    var ViewBase = function(dom, initOptions) {
        ViewBase.superclass.constructor.apply(this, arguments);

        this._ownerView = this;
    };
    violet.oo.extend(ViewBase, UIBase);
    // Export
    violet.ui = {
        'factory' : factory,
        'UIBase' : UIBase,
        'ViewBase' : ViewBase
    };
})();
