// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var RenderUtils = {};

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
        if (path.indexOf('://') === -1) {
            return appConfig.imageServerRoot + path;
        } else {
            return path;
        }
    };

    RenderUtils.imagePathToBackground = function(path) {
        return andrea.string.substitute('url("{0}")', RenderUtils.imagePathToURL(path));
    };

    RenderUtils.videoPathToURL = function(path) {
        if (path.indexOf('://') === -1) {
            return appConfig.videoServerRoot + path;
        } else {
            return path;
        }
    };

    RenderUtils.videoPathToBackground = function(path) {
        return andrea.string.substitute('url("{0}")', RenderUtils.videoPathToURL(path));
    };

    RenderUtils.hairTypeCodesToValue = function(codes) {
        
        var value = '';
        codes.split(',').forEach(function(code) {
            if (code == '0') {
                value = value + '所有,';
            } 
            if (code == '1') {
                value = value + '长发,';
            }
            if (code == '2') {
                value = value + '超长发,';
            }
            if (code == '3') {
                value = value + '中长发,';
            }
        });

        return value.substring(0, value.length - 1);
    };

    return RenderUtils;
});
