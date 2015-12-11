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

var items = db.getCollection('items').find({});

items.forEach(function(item) {
    if (item.source != null && item.source.length != 0) {

        var domain = getDomain(item.source);
        var id = '';

            domain = 'taobao';
            id = getIdFromSource(item.source, /id=(\d*)/);
            domain = 'tmall';
            id = getIdFromSource(item.source, /id=(\d*)/);
        } else if (domain.test(/thejamy/ig)) {
            domain = 'jamy';
            id = getIdFromSource(item.source, /product\/[a-zA-Z0-9]*/);
        } else if (domain.test(/hm/ig)) {
            domain = 'hm';
            id = getIdFromSource(item.source, /page.(\d*)/);
        } else {
            domain = '';
            id = '';
        }

        item.sourceInfo = {
            domain: domain,
            id: id
        }

        db.getCollection('items').save(item);
    }
});

