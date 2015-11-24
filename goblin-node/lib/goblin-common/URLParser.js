var URLParser = module.exports;

/*
 * http://detail.tmall.com/item.htm?spm=a1z10.4.w5003-9301197547.23.qjlW0g&id=40952601693&rn=2b5acab447292dedd6c7bc1d2e9a5387&abbucket=7&scene=taobao_shop
 * http://item.taobao.com/item.htm?spm=2013.1.w5734072-1564537832.10.Vg0Rmz&id=42888614090
 * */
URLParser.getIidFromSource = function (source) {
    var idRegex = /id=(\d*)/;
    var idComp = source.match(idRegex);
    if (idComp && idComp.length > 1) {
        return idComp[1];
    } else {
        return null;
    }
};

URLParser.isFromTmall = function (source) {
    var tmallRegex = /(http|https):\/\/\w*\.tmall\.com/;
    return source.match(tmallRegex);
};

URLParser.isFromTaobao = function (source) {
    var taobaoRegex = /(http|https):\/\/\w*\.taobao\.com/;

    return source.match(taobaoRegex);
};

URLParser.isFromHm = function (source) {
    var hmRegex = /(http|https):\/\/\w*2\.hm\.com/;
    return source.match(hmRegex);
};


URLParser.isFromJamy = function (source) {
    var jamyRegex = /(http|https):\/\/\w*\.thejamy\.com/;
    return source.match(jamyRegex);
};

