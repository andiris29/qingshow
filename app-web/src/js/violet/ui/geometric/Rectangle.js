// @formatter:off
define([
    
], function() {
// @formatter:on
    var Rectangle = function(left, top, width, height) {
        this._left = left;
        this._top = top;
        this._width = width;
        this._height = height;
    };

    Rectangle.parseDOM = function(dom) {
        var dom$ = $(dom), offset = dom$.offset();
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
