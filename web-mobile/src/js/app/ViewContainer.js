// @formatter:off
define([
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
