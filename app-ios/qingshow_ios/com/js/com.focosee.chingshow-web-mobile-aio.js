/* com.focosee.chingshow-web-mobile-0.2.0 */if (requirejs && requirejs.s && requirejs.s.contexts && requirejs.s.contexts._) {
    window.__andrea_internal_requirejs_nextTick__ = requirejs.s.contexts._.nextTick;
    requirejs.s.contexts._.nextTick = function(fn) {fn();};
}
define('ui/UIComponent',[], function() {
    var UIComponent = function(dom, data) {
        if (dom.get) {
            this._dom = dom.get(0);
            this._dom$ = dom;
        } else {
            this._dom = dom;
            this._dom$ = $(dom);
        }

        this._trigger$ = $({});
    };

    UIComponent.prototype.destroy = function() {
        this.trigger('destroying');
        this._dom$.remove();
        this.trigger('destroy');
        this._trigger$.off();
    };

    UIComponent.prototype.dom$ = function() {
        return this._dom$;
    };

    UIComponent.prototype.dom = function() {
        return this._dom;
    };

    UIComponent.prototype.hide = function() {
        this._dom$.hide();
    };

    UIComponent.prototype.show = function() {
        this._dom$.show();
    };

    UIComponent.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    UIComponent.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };

    UIComponent.prototype.getPreferredSize = function() {
    };

    return UIComponent;
});

// @formatter:off
define('ui/layout/Rectangle',[],
function() {
// @formatter:on

    var Rectangle = function(left, top, width, height) {
        this._left = left;
        this._top = top;
        this._width = width;
        this._height = height;
    };

    Rectangle.parseDOM = function(dom$) {
        var offset = dom$.offset();
        return new Rectangle(offset.left, offset.top, dom$.outerWidth(), dom$.outerHeight());
    };

    Rectangle.prototype.left = function(value) {
        if (arguments.length > 0) {
            this._left = value;
        } else {
            return this._left;
        }
    };

    Rectangle.prototype.top = function(value) {
        if (arguments.length > 0) {
            this._top = value;
        } else {
            return this._top;
        }
    };

    Rectangle.prototype.right = function(value) {
        if (arguments.length > 0) {
            this.left(value - this.width());
        } else {
            return this.left() + this.width();
        }
    };

    Rectangle.prototype.bottom = function(value) {
        if (arguments.length > 0) {
            this.top(value - this.height());
        } else {
            return this.top() + this.height();
        }
    };

    Rectangle.prototype.width = function() {
        return this._width;
    };

    Rectangle.prototype.height = function() {
        return this._height;
    };

    Rectangle.prototype.hCenter = function() {
        return this.left() + this.width() / 2;
    };

    Rectangle.prototype.vCenter = function() {
        return this.top() + this.height() / 2;
    };

    Rectangle.prototype.hContains = function(target) {
        return this.left() <= target.left() && this.right() >= target.right();
    };

    Rectangle.prototype.vContains = function(target) {
        return this.top() <= target.top() && this.bottom() >= target.bottom();
    };

    Rectangle.prototype.contains = function(target) {
        return this.hContains(target) && this.vContains(target);
    };

    Rectangle.toJSON = function(instance) {
        return {
            'left' : instance.left(),
            'top' : instance.top(),
            'width' : instance.width(),
            'height' : instance.height()
        };
    };

    return Rectangle;
});

// @formatter:off
define('ui/UIContainer',[
    'ui/UIComponent'
], function(UIComponent) {
// @formatter:on
    var UIContainer = function(dom, data) {
        UIContainer.superclass.constructor.apply(this, arguments);

        this._children = [];
    };
    andrea.oo.extend(UIContainer, UIComponent);

    UIContainer.prototype.destroy = function() {
        this._children.forEach(function(child) {
            child.destroy();
        });
        UIContainer.superclass.destroy.apply(this, arguments);
    };

    UIContainer.prototype.append = function(uiComponent) {
        uiComponent.dom$().appendTo(this.dom$());
        this._children.push(uiComponent);
    };

    UIContainer.prototype.delegate = function(eventName) {
        this._children.forEach( function(child) {
            child.on(eventName, this.trigger(eventName));
        }.bind(this));
    };

    return UIContainer;
});

// @formatter:off
define('ui/scroll/IScrollContainer',[
    'ui/UIContainer'
], function(UIContainer) {
// @formatter:on
    var IScrollContainer = function(dom, data) {
        IScrollContainer.superclass.constructor.apply(this, arguments);

        this._dom$.addClass('uiIScrollWrapper').css({
            'position' : 'absolute',
            'overflow' : 'hidden'
        });
        this._scroller$ = $('<div/>').addClass('uiIScroller').css({
            'position' : 'absolute'
        }).appendTo(this._dom$);

        this._iscroll = null;

        this._validateIScroll();
    };
    andrea.oo.extend(IScrollContainer, UIContainer);

    IScrollContainer.prototype._validateIScroll = function(uiComponent) {
        if (this._iscroll) {
            this._iscroll.destroy();
        }
        this._iscroll = new IScroll(this.dom(), {
            'mouseWheel' : true,
            'click' : true
        });
    };

    IScrollContainer.prototype.append = function(uiComponent) {
        uiComponent.dom$().appendTo(this._scroller$);
        uiComponent.on('resize', function() {
            this._validateIScroll();
        }.bind(this));
        this._children.push(uiComponent);
    };

    return IScrollContainer;
});

// @formatter:off
define('app/views/ViewBase',[
    'ui/UIComponent',
    'app/ViewContainer'
], function(UIComponent, ViewContainer) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var ViewBase = function(dom) {
        ViewBase.superclass.constructor.apply(this, arguments);
        this._dom$.addClass('qsView');
    };
    andrea.oo.extend(ViewBase, UIComponent);

    return ViewBase;
});

// @formatter:off
define('app/managers/TemplateManager',[
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var TemplateManager = {};

    var _cache = {};

    TemplateManager.load = function(src, debug, callback) {
        if (arguments.length === 2) {
            callback = debug;
            debug = false;
        }

        if (_cache[src]) {
            callback(null, _cache[src].clone());
        } else {
            $.ajax({
                'url' : './templates/' + src + (andrea.env.debug ? ('?' + Math.random()) : '')
            }).done(function(data) {
                data = data.replace('<link href="../libs/font-awesome-4.2.0/css/font-awesome.css" rel="stylesheet"/>', '');
                data = data.replace('<link href="../css/app.css" rel="stylesheet"/>', '');
                data = data.replace(new RegExp('./assets', 'g'), './templates/assets');
                data = data.replace(new RegExp('../../deps-fake/pics', 'g'), '../deps-fake/pics');
                var i = data.indexOf('<div id="wrapper"');
                if (i !== -1) {
                    data = data.substring(0, i) + data.substring(data.indexOf('>', i) + 1, data.lastIndexOf('</div>'));
                }
                var tplt$ = $(data);
                // Process style
                var style$ = tplt$.eq(0);
                style$.appendTo(document.head);
                // Process dom
                var dom$ = tplt$.eq(tplt$.length - 1);
                $('.fake', dom$).remove();
                if (!debug) {
                    $('.fakeText', dom$).removeClass('fakeText').text('');
                    $('.fakeSrc', dom$).removeClass('fakeSrc').attr('src', '');
                    $('.fakeBackgroundImage', dom$).removeClass('fakeBackgroundImage').css('background-image', 'none');
                    var fakeVideo$ = $('.fakeVideo', dom$);
                    if (fakeVideo$.length) {
                        fakeVideo$.removeAttr('poster');
                        $('source', fakeVideo$).remove();
                    }
                }
                _cache[src] = dom$;
                callback(null, dom$.clone());
            });
        }
    };

    return TemplateManager;
});

// @formatter:off
define('app/components/Header',[
    'ui/UIComponent',
    'app/managers/TemplateManager'
], function(UIComponent, TemplateManager) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Header = function(dom, data) {
        Header.superclass.constructor.apply(this, arguments);

        TemplateManager.load('header.html', function(err, content$) {
            this._dom$.append(content$);

            // Render
            $('.qsTitle', this._dom$).text(data);

            $('.qsBack', this._dom$).on(appRuntime.events.click, function() {
                appRuntime.view.back();
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(Header, UIComponent);

    Header.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    return Header;
});

// @formatter:off
define('app/services/DataService',[
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var DataService = {};

    DataService.request = function(path, requestData, callback) {
        var request;
        if (andrea.env.debug) {
            request = {
                'url' : '../deps-fake/data' + path + '.json'
            };
        } else {
            request = {
                'url' : appConfig.server.url + path,
                'type' : 'POST',
                'dataType' : 'json',
                'data' : requestData
            };
        }
        $.ajax(request).done(function(responseData) {
            console.log('api: ' + path, requestData, responseData);
            callback(null, responseData);
        }).fail(function(target, msg, err) {
            callback(msg);
        }).always(function() {
        });
    };

    return DataService;
});

// @formatter:off
define('app/utils/DateUtils',[
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var DateUtils = {};

    var _today;

    DateUtils.parseAge = function(time) {
        if (!_today) {
            _today = new Date();
        }
        var birth = new Date(time);
        var yearDiff = _today.getFullYear() - birth.getFullYear();
        var monthDiff = _today.getMonth() - birth.getMonth();
        var dateDiff = _today.getDate() - birth.getDate();
        if (monthDiff > 0 || (monthDiff === 0 && dateDiff >= 0)) {
            return yearDiff;
        } else {
            return yearDiff - 1;
        }
    };

    return DateUtils;
});

// @formatter:off
define('app/components/ItemGallery',[
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/DateUtils'
], function(UIComponent, TemplateManager, DataService, DateUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Item, People;
    var ItemGallery = function(dom) {
        ItemGallery.superclass.constructor.apply(this, arguments);
        require(['app/views/Item', 'app/views/People'], function(rItem, rPeople) {
            Item = rItem;
            People = rPeople;
        });

        this._tpltLiPeople$ = null;

        this._dom$.css('overflow', 'auto');
        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('item-gallery.html', function(err, content$) {
                content$.css('minHeight', this._dom$.height() + 'px');
                this._dom$.append(content$);

                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load template for list items
            TemplateManager.load('item-gallery-li.html', function(err, content$) {
                callback(null, content$);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load data
            DataService.request('/peoples', null, callback);
        }.bind(this)], function(err, results) {
            this._tpltLiPeople$ = results[1];
            this._render(results[2]);
        }.bind(this));
    };

    andrea.oo.extend(ItemGallery, UIComponent);

    ItemGallery.prototype._render = function(peoples) {
        var containers$ = $('.qsLiItemContainer', this._dom$);

        peoples.forEach( function(people, index) {
            var liPeople$ = this._renderPeople(this._tpltLiPeople$.clone(), people);
            var targetContainer$;
            containers$.each(function(index, container) {
                if (!targetContainer$ || targetContainer$.children().length > $(container).children().length) {
                    targetContainer$ = $(container);
                }
            });
            liPeople$.appendTo(targetContainer$);
        }.bind(this));

        $('img', this._dom).on('load', function() {
            this.trigger('resize');
        }.bind(this));
    };

    ItemGallery.prototype._renderPeople = function(liPeople$, people) {
        $('.qsItemCover', liPeople$).attr('src', people.cover);
        $('.qsItemDescription', liPeople$).text(people.itemDescription);
        $('.qsPortrait', liPeople$).css('background-image', andrea.string.substitute('url("{0}")', people.portrait));
        $('.qsName', liPeople$).text(people.name);
        $('.qsAge', liPeople$).text(DateUtils.parseAge(people.birthtime) + '岁');
        $('.qsStatus', liPeople$).text(people.status);
        $('.qsNumLikes', liPeople$).text(people.numLikes);

        $('.qsItemCover, .qsItemDescription', liPeople$).on('click', function() {
            appRuntime.view.to(Item);
        }.bind(this));
        $('.qsPeople', liPeople$).on('click', function() {
            appRuntime.view.to(People);
        }.bind(this));
        return liPeople$;
    };

    return ItemGallery;
});

// @formatter:off
define('app/views/Category',[
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/Header',
    'app/components/ItemGallery'
], function(IScrollContainer, ViewBase, Header, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Category = function(dom) {
        Category.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), '分类 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ItemGallery($('<div/>'));
        body.append(gallery);
    };
    andrea.oo.extend(Category, ViewBase);

    return Category;
});

// @formatter:off
define('app/components/CategoryMenu',[
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/views/Category'
], function(UIComponent, TemplateManager, Category) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var CategoryMenu = function(dom) {
        CategoryMenu.superclass.constructor.apply(this, arguments);

        async.parallel([
        function(callback) {
            TemplateManager.load('category-menu.html', callback);
        }], function(err, results) {
            var content$ = results[0];
            this._dom$.append(content$);

            $('li').on('click', function() {
                appRuntime.view.to(Category);
                appRuntime.popup.remove(this);
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(CategoryMenu, UIComponent);

    CategoryMenu.prototype.getPreferredSize = function() {
        return {
            'width' : 640,
            'height' : 510
        };
    };

    return CategoryMenu;
});

// @formatter:off
define('app/components/HomeHeader',[
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/components/CategoryMenu'
], function(UIComponent, TemplateManager, CategoryMenu) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var HomeHeader = function(dom) {
        HomeHeader.superclass.constructor.apply(this, arguments);

        TemplateManager.load('home-header.html', function(err, content$) {
            this._dom$.append(content$);

            var menu;
            $('.qsCategoryMenu', content$).on(appRuntime.events.click, function() {
                if (menu) {
                    return;
                }
                menu = appRuntime.popup.create(CategoryMenu, true);
                appRuntime.popup.dock(menu, this._dom$, 'lb', 'down', 0);
                menu.on('destroy', function() {
                    menu = null;
                });
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(HomeHeader, UIComponent);

    HomeHeader.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    return HomeHeader;
});

// @formatter:off
define('app/views/Home',[
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/HomeHeader',
    'app/components/ItemGallery',
], function(IScrollContainer, ViewBase, HomeHeader, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Home = function(dom) {
        Home.superclass.constructor.apply(this, arguments);

        var header = new HomeHeader($('<div/>').appendTo(this._dom$));
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ItemGallery($('<div/>'));
        body.append(gallery);
    };
    andrea.oo.extend(Home, ViewBase);

    return Home;
});

// @formatter:off
define('app/ViewContainer',[
    'ui/UIComponent',
    'app/views/Home'
], function(UIComponent, Home) {
// @formatter:on
    /**
     * View container, own the animation between view switch
     */
    var ViewContainer = function(dom) {
        ViewContainer.superclass.constructor.apply(this, arguments);
        this._dom$.addClass('qsViewContainer');

        this._views = [];
        this._currentView = null;

        appRuntime.view.to = $.proxy(this.to, this), appRuntime.view.back = $.proxy(this.back, this);
        appRuntime.view.to(Home);
    };
    andrea.oo.extend(ViewContainer, UIComponent);

    ViewContainer.prototype.to = function(clazz) {
        var view = new clazz($('<div/>').appendTo(this._dom$));
        this._views.push(view);
        // Render view
        if (this._currentView) {
            // Animation from right
            this._swapView(PageTransitions.animations.mobileNextPage, this._currentView, view, function() {
                this._currentView.hide();
                this._currentView = view;
            }.bind(this));
        } else {
            this._currentView = view;
        }
    };

    ViewContainer.prototype.back = function(callback) {
        if (this._views.length < 2) {
            return;
        }
        // Animation from left
        var view = this._views[this._views.length - 2];
        this._views.pop();

        this._swapView(PageTransitions.animations.mobilePrevPage, this._currentView, view, function() {
            this._currentView.destroy();
            this._currentView = view;
        }.bind(this));
    };

    ViewContainer.prototype._swapView = function(animation, view1, view2, callback) {
        view1.show(), view2.show();
        new PageTransitions(view1.dom$(), view2.dom$()).nextPage(animation, callback);
    };

    return ViewContainer;
});

// @formatter:off
define('ui/layout/DockToRectangle',[
], function() {
// @formatter:on
    var DockToRectangle = {};
    /**
     *
     * @param {UIComponent} ui
     * @param {Object} element$
     * @param {string} align lt/rt/lb/rb
     * @param {string} direction up/down/left/right
     * @param {int} gap
     */
    DockToRectangle.dock = function(ui, rectangle, align, direction, gap) {
        ui.dom$().css('position', 'absolute');

        if (direction === 'down' || direction === 'up') {
            ui.dom$().css('left', align.substr(0, 1) === 'l' ? rectangle.left() : rectangle.right());
            ui.dom$().css('top', direction === 'down' ? rectangle.bottom() + gap : rectangle.top() - gap);
        }
    };

    return DockToRectangle;
});

// @formatter:off
define('app/Root',[
    'ui/UIComponent',
    'ui/layout/Rectangle',
    'app/ViewContainer',
    'ui/layout/DockToRectangle'
], function(UIComponent, Rectangle, ViewContainer, DockToRectangle) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Root = function(dom, width, height) {
        Root.superclass.constructor.apply(this, arguments);
        this._dom$.addClass('qsRoot');

        // Set the size of root
        this._scale = width / 640;
        this._dom$.css({
            'width' : '640px',
            'height' : height / this._scale + 'px',
            '-webkit-transform' : andrea.string.substitute('scale({0}, {0})', this._scale)
        });

        // View container
        this._viewContainer = new ViewContainer($('<div/>').appendTo(this._dom$));
        // Popup
        appRuntime.popup.create = $.proxy(this.createPopup, this), appRuntime.popup.remove = $.proxy(this.removePopup, this);
        appRuntime.popup.dock = $.proxy(this.dockPopup, this);
        this._popups = [];
    };

    andrea.oo.extend(Root, UIComponent);

    Root.prototype.createPopup = function(clazz, closeClickOutside) {
        var popup = new clazz($('<div/>').addClass('qsPopup').appendTo(this._dom$));
        this._popups.push(popup);

        // Close when click outside
        if (closeClickOutside) {
            var html$ = $('html');
            var remove = function() {
                html$.off(appRuntime.events.click);
                appRuntime.popup.remove(popup);
            };
            _.defer(function() {
                html$.on(appRuntime.events.click, remove);
            });

            popup.dom$().on(appRuntime.events.click, function(event) {
                event.stopPropagation();
            });
        }
        // Animation
        new PageTransitions(null, popup.dom$()).nextPage(PageTransitions.animations.createPopup);

        return popup;
    };

    Root.prototype.removePopup = function(popup, callback) {
        this._popups.splice(this._popups.indexOf(popup), 1);

        // Animation
        new PageTransitions(popup.dom$(), null).nextPage(PageTransitions.animations.removePopup, function() {
            popup.destroy();
            if (callback) {
                callback();
            }
        });
    };

    Root.prototype.dockPopup = function(popup, element$, align, direction, gap) {
        var rootRectangle = Rectangle.parseDOM(this._dom$);
        var elementRectangle = Rectangle.parseDOM(element$);

        var left = Math.round((elementRectangle.left() - rootRectangle.left()) / this._scale);
        var top = Math.round((elementRectangle.top() - rootRectangle.top()) / this._scale);
        DockToRectangle.dock(popup, new Rectangle(left, top, elementRectangle.width(), elementRectangle.height()), align, direction, gap);
    };

    return Root;
});

// @formatter:off
define('app',[
    'app/Root',
], function(Root) {
// @formatter:on
    /**
     * Bootstrap the application
     */

    var dom = $(document.body).children()[0], dom$ = $(dom);
    // Ref: iPhone 4: 960 x 640
    // Ref: iPhone 5: 1136 x 640
    var screenW = $(window).width(), screenH = $(window).height();
    var ratio = screenW < screenH ? screenW / screenH : 640 / 960;
    var height = Math.max(0, screenH), width = ratio * height;

    dom$.css({
        'width' : width + 'px',
        'height' : height + 'px'
    });
    new Root($('<div/>').appendTo(dom$), width, height);
});

// @formatter:off
define('app/components/ItemMain',[
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService'
], function(UIComponent, TemplateManager, DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var ItemMain = function(dom) {
        ItemMain.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('item.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load template
            TemplateManager.load('item-main.html', true, function(err, content$) {
                callback(null, content$);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load data
            DataService.request('/item', null, callback);
        }.bind(this)], function(err, results) {
            var main$ = results[1];
            main$.appendTo($('.qsItemMain', this._dom$));

            this._render(results[2]);
        }.bind(this));

    };
    andrea.oo.extend(ItemMain, UIComponent);

    ItemMain.prototype._render = function(itemJSON) {

        videojs($('.qsItemVideo', this._dom$).get(0));
    };

    return ItemMain;
});

// @formatter:off
define('app/views/Item',[
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/Header',
    'app/components/ItemMain',
    'app/components/ItemGallery'
], function(IScrollContainer, ViewBase, Header, ItemMain, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Item = function(dom) {
        Item.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), '商品 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new ItemMain($('<div/>'));
        var gallery = new ItemGallery($('<div/>'));
        body.append(main);
        body.append(gallery);
    };
    andrea.oo.extend(Item, ViewBase);

    return Item;
});

// @formatter:off
define('app/views/People',[
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/Header',
    'app/components/ItemGallery',
], function(IScrollContainer, ViewBase, Header, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var People = function(dom) {
        People.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), '网红 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ItemGallery($('<div/>'));
        body.append(gallery);
    };
    andrea.oo.extend(People, ViewBase);

    return People;
});

//Export modules in the list to global scope
(function(global) {
    // @formatter:off
    var list = [
    ];
    // @formatter:on
    var jq = global.jQuery;

    if (jq) {
        if (jQuery.holdReady) {
            jQuery.holdReady(true);
        } else {
            jQuery.readyWait += 1;
        }
    }
    //put modules on global object
    require(list, function() {
        var exports = arguments;

        list.forEach(function(mod, i) {
            var nameParts = mod.split("/");
            nameParts.reduce(function(p, c, index) {
                if (!p[c]) {
                    if (index < nameParts.length - 1) {
                        p[c] = {};
                    } else {
                        p[c] = exports[i];
                    }
                }
                return p[c];
            }, global);
        });
        if (jq) {
            if (jQuery.holdReady) {
                jQuery.holdReady(false);
            } else {
                jQuery.ready(true);
            }
        }
    });

})(this);
if (window.__andrea_internal_requirejs_nextTick__ !== undefined) {
    if (requirejs && requirejs.s && requirejs.s.contexts && requirejs.s.contexts._) {
        requirejs.s.contexts._.nextTick = window.__andrea_internal_requirejs_nextTick__;
    }
    window.__andrea_internal_requirejs_nextTick__ = undefined;
}