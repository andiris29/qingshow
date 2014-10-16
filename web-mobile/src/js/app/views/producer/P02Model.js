// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/show/ShowGallery',
], function(IScrollContainer, ViewBase, CommonHeader, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var P02Model = function(dom, data) {
        P02Model.superclass.constructor.apply(this, arguments);
        this._model = data.model;

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), this._model.name);
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ItemGallery($('<div/>'));
        body.append(gallery);
    };
    andrea.oo.extend(P02Model, ViewBase);

    return P02Model;
});
