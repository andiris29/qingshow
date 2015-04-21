// @formatter:off
define([
    'qingshow/services/HTTPService'
], function(HTTPService) {
// @formatter:on
    return {
        'create' : function() {
            HTTPService.post('/trade/create', {
                'totalFee' : 412,
                'orders' : [{
                    'quantity' : 2,
                    'price' : 206,
                    'itemSnapshot' : {
                        "_id" : "54c0dc41c3a8a6398f32ee2b",
                        "category" : 3,
                        "name" : "英伦格子牛角斗篷仿羊绒高领百搭女士新年披肩",
                        "price" : 206,
                        "source" : "http://detail.tmall.com/item.htm?spm=a1z10.3-b.w4011-2717863134.78.0EJ5nn&id=42532668723&rn=a11ee2719a3d2ac501ec7dcfdc49763e&abbucket=7",
                        "itemid" : "a20314",
                        "brandRef" : {
                            "_id" : "54c0dd2bc3a8a6398f32ee39",
                            "type" : 0,
                            "name" : "天猫南秀丝语旗舰店",
                            "logo" : "http://trial01.focosee.com/demo2/a203.jpg",
                            "background" : "http://trial01.focosee.com/demo2/a203_bg.jpg",
                            "cover" : "http://trial01.focosee.com/demo2/a203_ad.jpg",
                            "create" : "2015-03-17T10:27:47.457Z",
                            "coverMetadata" : {
                                "url" : "http://trial01.focosee.com/demo2/a203_ad.jpg",
                                "width" : 640,
                                "height" : 640
                            }
                        },
                        "taobaoInfo" : {
                            "skus" : [{
                                "sku_id" : 71601754696,
                                "properties" : ";1627207:132069;",
                                "properties_name" : "RP114AL驼色",
                                "price" : 296,
                                "promo_price" : 208,
                                "stock" : 10,
                                "properties_thumbnail" : "http://gi4.md.alicdn.com/bao/uploaded/i4/458526499/TB2oHrabXXXXXcYXXXXXXXXXXXX_!!458526499.jpg_40x40q90.jpg"
                            }, {
                                "sku_id" : 71601754697,
                                "properties" : ";1627207:3232478;",
                                "properties_name" : "RP114BL灰色",
                                "price" : 296,
                                "promo_price" : 208,
                                "stock" : 20,
                                "properties_thumbnail" : "http://gi2.md.alicdn.com/bao/uploaded/i2/458526499/TB2A6TXbXXXXXXsXpXXXXXXXXXX_!!458526499.jpg_40x40q90.jpg"
                            }, {
                                "sku_id" : 71601754698,
                                "properties" : ";1627207:28326;",
                                "properties_name" : "RP114CL红色",
                                "price" : 296,
                                "promo_price" : 208,
                                "stock" : 20,
                                "properties_thumbnail" : "http://gi2.md.alicdn.com/bao/uploaded/i2/458526499/TB2XsnabXXXXXc6XXXXXXXXXXXX_!!458526499.jpg_40x40q90.jpg"
                            }],
                            "top_title" : "南秀丝语2014新款英伦格子牛角斗篷仿羊绒高领百搭女士新年披肩",
                            "top_nick" : "南秀丝语旗舰店",
                            "top_num_iid" : "42532668723",
                            "refreshTime" : "2015-03-16T17:01:49.595Z"
                        },
                        "create" : "2015-03-17T10:27:47.447Z",
                        "brandNewInfo" : {
                            "order" : 14
                        },
                        "imageMetadata" : {
                            "url" : "http://trial01.focosee.com/demo2/a2031401.jpg",
                            "width" : 640,
                            "height" : 950
                        },
                        "images" : [{
                            "url" : "http://trial01.focosee.com/demo2/a2031401.jpg",
                            "description" : "英伦格子牛角斗篷仿羊绒高领百搭女士新年披肩"
                        }, {
                            "url" : "http://trial01.focosee.com/demo2/a2031402.jpg",
                            "description" : "斗篷式披肩，时尚大气"
                        }, {
                            "url" : "http://trial01.focosee.com/demo2/a2031403.jpg",
                            "description" : "英伦格，百搭款式"
                        }, {
                            "url" : "http://trial01.focosee.com/demo2/a2031404.jpg",
                            "description" : "仿羊绒材质，牛角斗篷"
                        }]
                    }
                }],
                'selectedItemSkuId' : 2,
                'selectedPeopleReceiverUuid' : 'xxx'
            });
        },
        'queryCreatedBy' : function() {
            HTTPService.get('/trade/queryCreatedBy', {
                '_id' : '5496cb7fc05ab9282a6df5dc'
            });
        }
    };
});
