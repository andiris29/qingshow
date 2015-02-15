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
                    data = data.replace(/<<script src.*>/g, '');
                    data = data.replace(/(\.\/|\.\.\/).*assets/g, './assets');
                    // Handling
                    var tplt$ = $(data);
                    // Handle style
                    var style$ = tplt$.eq(0);
                    style$.appendTo(document.head);
                    // Handle dom
                    var dom$ = tplt$.eq(tplt$.length - 1);

                    _clearFake('fake', dom$);
                    _clearFake('fake-text', dom$);
                    _clearFake('fake-css-display', dom$);
                    _clearFake('fake-css-background-image', dom$);
                    _clearFake('fake-attr-src', dom$);
                    _clearFake('fake-attr-poster', dom$);
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
    var _clearFake = function(style, dom$) {
        var fake$ = $('.' + style, dom$);
        if (fake$.length === 0) {
            return;
        }
        fake$.removeClass(style);
        if (style === 'fake') {
            fake$.remove();
        } else if (style === 'fake-text') {
            fake$.text('');
        } else if (style.indexOf('fake-css-') !== -1) {
            fake$.css(style.substr('fake-css-'.length), '');
        } else if (style.indexOf('fake-attr-') !== -1) {
            fake$.removeAttr(style.substr('fake-attr-'.length));
        }
    };

    return TemplateService;
});
