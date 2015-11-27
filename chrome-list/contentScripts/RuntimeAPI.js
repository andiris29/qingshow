var source = window.location.href;

var htmlText = "<div id='pop' style='background-color:#0000ff;";
htmlText += "position: absolute;"
htmlText += "left: 40%;";
htmlText += "'>";



chrome.runtime.sendMessage({
    method: 'GET',
    action: 'xhttp',
    data: source
}, function(responseText) {
    var response = JSON.parse(responseText);

    if (response.data != null) {
        console.log("该商品已上架");
        htmlText += "该商品已上架";
    } else if (response.error = "1004") {
        console.log("该商品未上架");
        htmlText += "该商品未上架";
        var item = {
            source: source,
            sourceInfo: {
                domain: getDomain(source),
                id: getIdFromSource(source, /id=(\d*)/)
            },
            syncEnabled: true
        };

        console.log(JSON.stringify(item, null, 4));
        htmlText += "<br>";
        htmlText += JSON.stringify(item, null, 4);
    } else {
        console.log("服务器端，执行错误");
        htmlText += "服务器端，执行错误";
    }
    htmlText += "</div>";
    $("#page").prepend(htmlText);
});

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
}

