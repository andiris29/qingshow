// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var ImageUtils = {};

    ImageUtils.formatURL = function(path) {
        if (path.indexOf('://') === -1) {
            return appConfig.imageServerRoot + path;
        } else {
            return path;
        }
    };

    ImageUtils.formatBackgroundImage = function(path) {
        return andrea.string.substitute('url("{0}")', ImageUtils.formatURL(path));
    };

    return ImageUtils;
});
