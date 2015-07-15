// @formatter:off
define([
    'main/views/View',
    'main/views/components/p04/StatusTo2Detail',
    'main/views/components/p04/StatusTo3Detail',
    'main/views/components/p04/StatusToEndDetail',
    'main/services/codeMongoService'
], function(
    View,
    StatusTo2Detail,
    StatusTo3Detail,
    StatusToEndDetail,
    codeMongoService
) {
// @formatter:on
    var P04EditTrade = function(dom, initOptions) {
        P04EditTrade.superclass.constructor.apply(this, arguments);

        this._statusToHanlder = null;

        var trade = initOptions.trade;
        // [1, 2, 7, 11, 16]
        var status$ = $('#status', this._dom);
        _statusToMap[trade.status].forEach(function(newStatus) {
            var option$ = $(document.createElement('option'));
            option$.attr('value', newStatus);
            option$.text(codeMongoService.toNameWithCode('trade.status', newStatus));
            status$.append(option$);
        });
        status$.on('change', this._render.bind(this));
        this._render();

        $('pre', this._dom).get(0).innerHTML = _syntaxHighlight(JSON.stringify(trade, null, 4));

        $('button', this._dom).on('click', this._submit.bind(this));
    };

    violet.oo.extend(P04EditTrade, View);

    P04EditTrade.prototype._render = function() {
        var status$ = $('#status', this._dom),
            newStatus = parseInt(status$.val());

        if (newStatus === 2) {
            this._statusToHanlder = new StatusTo2Detail(this);
        } else if (newStatus === 3 || newStatus === 14) {
            this._statusToHanlder = new StatusTo3Detail(this);
        } else {
            this._statusToHanlder = new StatusToEndDetail(this);
        }
        this._statusToHanlder.render();
    };

    P04EditTrade.prototype._submit = function() {
        var details = this._statusToHanlder.getDetails();
        if (!details) {
            return;
        }
        var trade = this._initOptions.trade,
            status$ = $('#status', this._dom),
            newStatus = parseInt(status$.val());

        this.request('/trade/statusTo', 'post', $.extend({
            '_id' : trade._id,
            'status' : newStatus
        }, details), function(err, metadata, data) {
            if (err) {
                alertify.error('编辑失败');
            } else if (metadata.error) {
                alertify.error('编辑失败－' + metadata.error);
            } else {
                alertify.success('编辑成功');
                this.pop();
            }
        }.bind(this));
    };

    var _statusToMap = {
        1 : [2, 17],
        2 : [3, 17],
        7 : [9, 10],
        11 : [13, 14],
        16 : [13, 14]
    };

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
