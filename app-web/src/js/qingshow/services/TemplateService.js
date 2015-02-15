// @formatter:off
define([
], function() {
// @formatter:on
    var TemplateService = {};

    var _cache = {};

    TemplateService.load = function(url, callback) {
        if (_cache[url]) {
            setTimeout(function() {
                callback(null, _cache[url].clone());
            }, 0);
        } else {
            $.ajax({
                'url' : url
            }).done(function(data) {
                // Build cache
                if (!_cache[url]) {
                    // Pre-handling
                    data = data.replace(/<meta.*>/g, '');
                    data = data.replace(/<link.*>/g, '');
                    data = data.replace(/(\.\/|\.\.\/).*assets/g, './assets');
                    // Handling
                    var tplt$ = $(data);
                    // Handle style
                    var style$ = tplt$.eq(0);
                    style$.appendTo(document.head);
                    // Handle dom
                    var dom$ = tplt$.eq(tplt$.length - 1);
                    $('.fake', dom$).remove();
                    $('.fakeText', dom$).removeClass('fakeText').text('');
                    $('.fakeCssDisplay', dom$).removeClass('fakeCssDisplay').css('display', '');
                    $('.fakeCssBackgroundImage', dom$).removeClass('fakeCssBackgroundImage').css('background-image', 'none');
                    $('.fakeAttrSrc', dom$).removeClass('fakeSrc').removeAttr('src');
                    $('.fakeAttrPoster', dom$).removeClass('fakeAttrPoster').removeAttr('poster');
                    _cache[url] = dom$;
                }
            }).always(function() {
                if (_cache[url]) {
                    callback(null, _cache[url].clone().get(0));
                } else {
                    callback();
                }
            });
        }
    };

    return TemplateService;
});
