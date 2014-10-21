(function() {
    var PageTransitions = function($currPage, $nextPage) {
        this._$currPage = $currPage || $(null);
        this._$nextPage = $nextPage || $(null);

        this._isAnimating = false;
        this._endCurrPage = false;
        this._endNextPage = false;
    };

    var animEndEventNames = {
        'WebkitAnimation' : 'webkitAnimationEnd',
        'OAnimation' : 'oAnimationEnd',
        'msAnimation' : 'MSAnimationEnd',
        'animation' : 'animationend'
    }, animEndEventName = animEndEventNames[Modernizr.prefixed('animation')];

    PageTransitions.prototype.nextPage = function(animation, callback) {
        if (this._isAnimating) {
            return false;
        }

        this._isAnimating = true;

        this._$currPage.data('originalClassList', this._$currPage.attr('class'));
        this._$nextPage.data('originalClassList', this._$nextPage.attr('class'));

        var outClass = '', inClass = '';

        switch( animation ) {
            case 101:
                outClass = 'pt-page-andrea-moveToLeft-1-3';
                inClass = 'pt-page-moveFromRight';
                break;
            case 102:
                outClass = 'pt-page-moveToRight';
                inClass = 'pt-page-andrea-moveFromLeft-1-3';
                break;
            case 103:
                outClass = 'pt-page-moveToLeft';
                inClass = 'pt-page-moveFromRight';
                break;
            case 104:
                outClass = 'pt-page-moveToRight';
                inClass = 'pt-page-moveFromLeft';
                break;
            case 105:
                outClass = '';
                inClass = 'pt-page-andrea-popUp';
                break;
            case 106:
                outClass = 'pt-page-andrea-popDown';
                inClass = '';
                break;
        }

        this._$currPage.addClass(outClass).on(animEndEventName, function() {
            this._$currPage.off(animEndEventName);
            this._endCurrPage = true;
            if (this._endNextPage) {
                this._onEndAnimation(this._$currPage, this._$nextPage, callback);
            }
        }.bind(this));
        if (this._$currPage.length === 0) {
            this._endCurrPage = true;
        }

        this._$nextPage.addClass(inClass).on(animEndEventName, function() {
            this._$nextPage.off(animEndEventName);
            this._endNextPage = true;
            if (this._endCurrPage) {
                this._onEndAnimation(this._$currPage, this._$nextPage, callback);
            }
        }.bind(this));
        if (this._$nextPage.length === 0) {
            this._endNextPage = true;
        }
    };

    PageTransitions.prototype._onEndAnimation = function($outpage, $inpage, callback) {
        this._endCurrPage = false;
        this._endNextPage = false;
        this._resetPage($outpage, $inpage);
        this._isAnimating = false;
        if (callback) {
            callback();
        }
    };

    PageTransitions.prototype._resetPage = function($outpage, $inpage) {
        if ($outpage.data('originalClassList')) {
            $outpage.attr('class', $outpage.data('originalClassList'));
        } else {
            $outpage.removeAttr('class');
        }
        if ($inpage.data('originalClassList')) {
            $inpage.attr('class', $inpage.data('originalClassList'));
        } else {
            $inpage.removeAttr('class');
        }
    };

    PageTransitions.animations = {
        'nextView' : 101,
        'prevView' : 102,
        'nextTab' : 103,
        'prevTab' : 104,
        'createPopup' : 105,
        'removePopup' : 106
    };
    window.PageTransitions = PageTransitions;
})();

