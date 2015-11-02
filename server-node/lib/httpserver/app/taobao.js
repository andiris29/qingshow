var request = require('request');
var Iconv = require('iconv-lite');
var URLParser = require('../../goblin-slave/URLParser');

var taobao = module.exports;

taobao.get = {
	'method' : 'post',
	'func' : function(req, res){
		var qsParam = req.body;
		var url = 'http://item.taobao.com/item.htm?id=' + URLParser.getIidFromSource(qsParam.url);
		console.log(url);
		request.get({
			'url' : url,
			'headers' : qsParam.headers,
			'encoding' : 'binary'
		}, function(err, response, body){
			var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');
			res.send(b);
			res.end();
		}).setMaxListeners(0);
	}
}