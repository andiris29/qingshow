(function() {
    var violet = window.violet = {};
    // ------------------
    // violet.oo
    // ------------------
    var extend = function(subClz, superClz) {
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
        'extend' : extend
    };
    // ------------------
    // violet.string
    // ------------------
    var substitute = function(str, rest) {
        if (!str) {
            return '';
        }
        for (var i = 1; i < arguments.length; i++) {
            str = str.replace(new RegExp("\\{" + (i - 1) + "\\}", "g"), arguments[i]);
        }
        return str;
    };

    violet.string = {
        'substitute' : substitute
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
        },
            _dependencies = {},
            _cache = {};

        uiFactory.config = function(value) {
            _config = value;
        };

        uiFactory.registerDependencies = function(module, depModules) {
            _dependencies[module] = depModules;
        };

        uiFactory.createViewAsync = function(module, initOptions, parent, callback) {
            uiFactory.createUiAsync(module, initOptions, parent, null, callback);
        };

        uiFactory.createUiAsync = function(module, initOptions, parent, ownerView, callback) {
            uiFactory.load(module, function() {
                if (callback) {
                    callback(null, uiFactory.createUi(module, initOptions, parent, ownerView));
                }
            });
        };

        uiFactory.createUi = function(module, initOptions, parent, ownerView) {
            if (!_cache[module]) {
                throw 'You must load the module at first: ' + module;
            }

            var cache = _cache[module];
            var ui = new cache.UiClass(cache.dom$.clone().get(0), initOptions, ownerView);
            _generateSubModule(ui, ui.ownerView());

            if (parent) {
                ui.dom$().appendTo(parent);
            }
            return ui;
        };

        uiFactory.load = function(module, callback) {
            // Multiple
            if (_.isArray(module)) {
                async.parallel(module.map(function(module) {
                    return function(callback) {
                        uiFactory.load(module, callback);
                    };
                }), callback);
                return;
            }
            // Single
            if (_cache[module]) {
                callback(null);
            } else {
                // Load module
                _load(module, function(err) {
                    var cache = _cache[module],
                        dom$ = cache.dom$;

                    var dependencies = _dependencies[module] || [];
                    // Parse sub modules as dependencies
                    $('[violet-module]', dom$).each(function(index, placeholder) {
                        var def = $(placeholder).attr('violet-module').split(' as ');
                        dependencies.push(def[0]);
                    });
                    if (dependencies.length) {
                        _dependencies[module] = dependencies;
                    }
                    // Load dependent modules
                    async.parallel(dependencies.map(function(depModule) {
                        return function(callback) {
                            uiFactory.load(depModule, callback);
                        };
                    }), callback);
                });
            }
        };

        var _load = function(module, callback) {
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
                callback();
            });

        };

        var _generateUi = function(module, initOptions, ownerView) {
            var cache = _cache[module];
            var ui = new cache.UiClass(cache.dom$.clone().get(0), initOptions, ownerView);

            _generateSubModule(ui, ui.ownerView());

            return ui;
        };

        var _generateSubModule = function(ui, ownerView) {
            $('[violet-module]', ui.dom$()).each(function(index, placeholder) {
                var placeholder$ = $(placeholder);

                var def = placeholder$.attr('violet-module').split(' as '),
                    subModule = def[0],
                    subModuleInstanceName = def[1];

                var initOptions;
                try {
                    eval('initOptions = ' + placeholder$.attr('violet-initOptions'));
                } catch(err) {
                    initOptions = null;
                }

                var cache = _cache[subModule];
                var subUi = new cache.UiClass(cache.dom$.clone().get(0), initOptions, ownerView);
                subUi.dom$().appendTo(placeholder$);
                ui[subModuleInstanceName] = subUi;

                _generateSubModule(subUi, ownerView);
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
        if (this._ownerView) {
            this._ownerView.one('destroying', this.destroy.bind(this));
        }

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

    UIBase.prototype.one = function() {
        this._trigger$.one.apply(this._trigger$, arguments);
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
