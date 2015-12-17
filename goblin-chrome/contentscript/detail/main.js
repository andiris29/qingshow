
var __popup = function(dom$, json) {
    json = {
        'source' : json.source,
        'name' : json.name,
        'thumbnail' : 'TBD',
        'categoryRef' : 'ObjectId()',
        'expectable' : {
            'reduction' : 1,
            'message' : ''
        },
        'remixCategoryAliases' : 'TBD'
    };
    var popup$ = $('<div id="qs-json" style="display:none"><pre></pre></div>').appendTo(document.body);
    $('pre', popup$).get(0).innerHTML = _syntaxHighlight(json);

    dom$.popup({'content' : popup$});
};

var _syntaxHighlight = function(json) {
    json = JSON.stringify(json, null, 4);
    json = json.replace('"ObjectId()"', 'ObjectId()');

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