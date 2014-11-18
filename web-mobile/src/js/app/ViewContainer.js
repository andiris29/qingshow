// @formatter:off
define([
    'ui/UIComponent',
    'app/model'
], function(UIComponent, model) {
// @formatter:on
    /**
     * View container, own the animation between view switch
     */
    var ViewContainer = function(dom) {
        ViewContainer.superclass.constructor.apply(this, arguments);
        this._dom$.addClass('qsViewContainer');

        this._views = [];
        this._currentView = null;
        this._animating = false;

        appRuntime.view.to = $.proxy(this.to, this), appRuntime.view.back = $.proxy(this.back, this);

        if (andrea.env.uriFragment.entry === 'S03') {
            model.getShow(andrea.env.uriFragment._id, function(show) {
                if (show) {
                    appRuntime.view.to('app/views/show/S03Show', {
                        'show' : show
                    });
                } else {
                    appRuntime.view.to('app/views/show/S01Home');
                }
            });
        } else if (andrea.env.uriFragment.entry === 'P05') {
            appRuntime.view.to('app/views/producer/P05Collocation');
        } else {
            appRuntime.view.to('app/views/show/S01Home');
        }
    };
    andrea.oo.extend(ViewContainer, UIComponent);

    ViewContainer.prototype.to = function(clazz, data) {
        if (_.isString(clazz)) {
            var args = Array.prototype.slice.call(arguments, 0);
            require([clazz], function(clazz) {
                args[0] = clazz;
                ViewContainer.prototype.to.apply(this, args);
            }.bind(this));
        } else {
            if (this._animating) {
                return;
            }
            var view = new clazz($('<div/>').appendTo(this._dom$), data);
            this._views.push(view);
            // Render view
            if (this._currentView) {
                // Animation from right
                this._animating = true;
                this._swapView(PageTransitions.animations.nextView, this._currentView, view, function() {
                    this._animating = false;
                    this._postTo(view);
                }.bind(this));
            } else {
                this._postTo(view);
            }
        }
    };

    ViewContainer.prototype.back = function(callback) {
        if (this._views.length < 2 || this._animating) {
            return;
        }
        // Animation from left
        var view = this._views[this._views.length - 2];
        this._views.pop();

        this._animating = true;
        view.activate();
        this._swapView(PageTransitions.animations.prevView, this._currentView, view, function() {
            this._animating = false;

            this._postBack(view);
        }.bind(this));
    };

    ViewContainer.prototype._swapView = function(animation, view1, view2, callback) {
        new PageTransitions(view1.dom$(), view2.dom$()).nextPage(animation, callback);
    };

    ViewContainer.prototype._postTo = function(view) {
        if (this._currentView) {
            this._currentView.deactivate();
        }
        this._postSwap(view);
    };

    ViewContainer.prototype._postBack = function(view) {
        this._currentView.destroy();
        this._postSwap(view);
    };

    ViewContainer.prototype._postSwap = function(view) {
        this._currentView = view;

        if (this._currentView.loginRequired && !model.user()) {
            appRuntime.view.to('app/views/user/U06Login');
        }
    };
    return ViewContainer;
});
