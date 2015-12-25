function getDomain(url) {
    var domain;
    //find & remove protocol (http, ftp, etc.) and get domain
    if (url.indexOf("://") > -1) {
        domain = url.split('/')[2];
    } else {
        domain = url.split('/')[0];
    }

    //find & remove port number
    domain = domain.split(':')[0];
    return domain;
}

function getIdFromSource(source, idRegex) {
    var idComp = source.match(idRegex);
    if (idComp && idComp.length > 1) {
        return idComp[1];
    } else {
        return null;
    }
};

var items = db.getCollection('items').find({
});


items.forEach(function(item) {
    if (item.source != null && item.source.length != 0) {

        var domain = getDomain(item.source);
        var id = '';

        if (domain.indexOf('taobao') >= 0) {
            domain = 'taobao';
            id = getIdFromSource(item.source, /id=(\d*)/);
        } else if (domain.indexOf('tmall') >= 0) {
            domain = 'tmall';
            id = getIdFromSource(item.source, /id=(\d*)/);
        } else if (domain.indexOf('thejamy') >= 0) {
            domain = 'jamy';
            var urls = item.source.split('/');
            id = urls[urls.length-1];
        } else if (domain.indexOf('hm') >= 0) {
            domain = 'hm';
            id = getIdFromSource(item.source, /page.(\d*)/);
        } else {
            return;
        }

        item.sourceInfo = {
            'domain': domain,
            'id': id,
            'icon' : 'http://trial01.focosee.com/img/item/source/' + domain + '.png'
        }

        db.getCollection('items').save(item);
    }
});

