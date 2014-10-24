// @formatter:off
define([
    'app/utils/CodeUtils'
], function(CodeUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var RenderUtils = {};

    RenderUtils.PASSWORD_REGEXP = "^[A-Za-z0-9]{8,12}$";
    RenderUtils.EMAIL_REGEXP = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    RenderUtils.MOBILE_REGEXP= "^1[0-9]{10}$";

    var _today;
    RenderUtils.timeToAge = function(time) {
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

    RenderUtils.imagePathToURL = function(path) {
        if (path && path.indexOf('://') === -1) {
            return (andrea.env.fake ? appConfig.fake.imageServerRoot : appConfig.imageServerRoot) + path;
        } else {
            return path;
        }
    };

    RenderUtils.imagePathToBackground = function(path) {
        return andrea.string.substitute('url("{0}")', RenderUtils.imagePathToURL(path));
    };

    RenderUtils.videoPathToURL = function(path) {
        if (path && path.indexOf('://') === -1) {
            return (andrea.env.fake ? appConfig.fake.videoServerRoot : appConfig.videoServerRoot) + path;
        } else {
            return path;
        }
    };

    RenderUtils.videoPathToBackground = function(path) {
        return andrea.string.substitute('url("{0}")', RenderUtils.videoPathToURL(path));
    };

    RenderUtils.heightToDisplay = function(height) {
        return height ? height + 'cm' : '';
    };
    RenderUtils.weightToDisplay = function(weight) {
        return weight ? weight + 'kg' : '';
    };

    RenderUtils.checkStringMatchPattern = function(pattern, value) {

        var regExp= new RegExp(pattern);

        if (!regExp.test(value)) {
            return false;
        }

        return true;
    };

    return RenderUtils;
});
