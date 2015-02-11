var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var topShopSchema = Schema({
    "nick" : String,
    "desc" : String,
    "pic_path" : String,
    "shop_score" : {
        "delivery_score" : Number,
        "item_score" : Number,
        "service_score" : Number
    },
    "sid" : Number,
    "title" : String,
    "__context" : Object
});
var TopShop = mongoose.model('topShops', topShopSchema);
module.exports = TopShop;

/*
*
* "shop": {
 "desc": "<p>&nbsp;</p><div>亲，您好！欢迎光顾本店，我们将以最优质的产品为您提供一流的服务。我们始终相信， 善待顾客就是善待自己。如果商品用得好，请您把它介绍给您的朋友；如果商品用得不好，请您跟我们说。您的满意，是我们永远的追求。我们将用最大的努力，为您带去网购的乐趣！期盼亲的再次光临。&nbsp;</div><div>&nbsp;</div>",
 "nick": "粉白色小猪哒",
 "pic_path": "/3c/23/TB1b3nwFVXXXXcRXXXXSutbFXXX.jpg",
 "shop_score": {
 "delivery_score": "4.7",
 "item_score": "4.7",
 "service_score": "4.8"
 },
 "sid": 105957301,
 "title": "七号啦啦"
 }
*
* */