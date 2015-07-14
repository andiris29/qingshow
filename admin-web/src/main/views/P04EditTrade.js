// @formatter:off
define([
    'main/views/View'
], function(
    View
) {
// @formatter:on
    var P04EditTrade = function(dom, initOptions) {
        P04EditTrade.superclass.constructor.apply(this, arguments);

        $('pre', this._dom).get(0).innerHTML = _syntaxHighlight(JSON.stringify(initOptions.trade, null, 4));
    };

    violet.oo.extend(P04EditTrade, View);

    var _syntaxHighlight = function(json) {
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function(match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    };

    return P04EditTrade;
});
