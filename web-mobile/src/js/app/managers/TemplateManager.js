// @formatter:off
define([
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
                'url' : './templates/' + src,
                'cache' : !andrea.env.debug
            }).done(function(data) {
                data = data.replace(/<link.*>/g, '');
                data = data.replace(/(\.\/|\.\.\/).*templates/g, './templates');
                data = data.replace(/(\.\/|\.\.\/).*server-video-fake/g, '../../server-video-fake');
                data = data.replace(/(\.\/|\.\.\/).*server-image-fake/g, '../../server-image-fake');
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
