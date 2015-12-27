// @formatter:off
define([
], function(
) {
// @formatter:on
    var P05ShareItems = function(dom, initOptions) {
        P05ShareItems.superclass.constructor.apply(this, arguments);
       

          var _itemid = initOptions._itemid;
          pageLoadCall(_itemid);

         __services.httpService.request('/matcher/queryShopItems', 'get', {
          "itemRef":_itemid,
          "pageNo":1,
          "pageSize":10
          }, function(err, metadata, data) {
            if(!err)
            {
                if(data && data.items)
                {
                    var strListHTML = "";
                    $.each(data.items, function(index){  
                         if(this.thumbnail)
                         {
                            if(this.taobaoInfo && this.taobaoInfo.nick)
                            {
                                $(".shopwin-title").html(this.taobaoInfo.nick);
                            }
                            strListHTML += "<div class=\"win pull-left\">";
                            strListHTML +=      "<div class=\"win-thumbnail\">";
                            strListHTML +=          "<img src=\""+this.thumbnail+"\" class=\"win-img\" />";
                             if(this.promoPrice != this.price)
                            {
                                var discount = parseInt(this.promoPrice / this.price * 10);
                                if(discount<10)
                                {
                                    strListHTML +=          "<span class=\"flag-label\">"+discount+"折</span>";
                                }
                            }
                            strListHTML +=      "</div>";
                            strListHTML +=      "<div class=\"win-info\">";
                            strListHTML +=          "<p class=\"win-title\">"+this.name+"</p>";
                            if(this.promoPrice != this.price)
                            {
                                strListHTML +=          "<p class=\"price clearfix\"><em class=\"pull-left\">￥"+this.promoPrice+"</em> <del class=\"pull-right\">￥"+this.price+"</del></p>";
                            }
                            else
                            {
                                strListHTML +=          "<p class=\"price clearfix\"><em class=\"pull-left\" style='dis'>￥"+this.promoPrice+"</em> </p>";
                            }
                            strListHTML +=      "</div>";
                            strListHTML += "</div>";
                         }
                    });
                    $("#winItemList").html(strListHTML); 

                }
                else
                {
                    __services.navigationService.push('qs/views/P01NotFound');
                }
            }
        });


    };

    function pageLoadCall(showid)
    {
     var search = violet.url.search;
     __services.httpService.request('/user/loginAsViewer', 'post', {
     }, function(err, metadata, data) {});

     if(showid != "")
     {
         __services.httpService.request('/show/view', 'post', {
            '_id' : showid
        }, function(err, metadata, data) {});
     }
    }
    violet.oo.extend(P05ShareItems, violet.ui.ViewBase);

    return P05ShareItems;
});
