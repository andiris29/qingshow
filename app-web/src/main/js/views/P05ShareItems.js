// @formatter:off
define([
], function(
) {
// @formatter:on
    var P05ShareItems = function(dom, initOptions) {
        P05ShareItems.superclass.constructor.apply(this, arguments);
       
          $("div[name='mask']").show();
          var _itemid = initOptions._itemid;
          var _title = initOptions._title;
          pageLoadCall(_itemid);

         __services.httpService.request('/matcher/queryShopItems', 'get', {
          "itemRef":_itemid,
          "pageNo":1,
          "pageSize":1000
          }, function(err, metadata, data) {

            $("div[name='mask']").show();
            $(".topback").on("click",function(){
              __services.navigationService.pop();
            });

            if(!err)
            {

                $(".shopwin-title").html(_title);
                if(data && data.items)
                {
                    var strListHTML = "";
                    $.each(data.items, function(index){  
                         if(this.thumbnail )
                         {
                            if(this.shopref && this.shopref.nickname)
                            {
                                $(".shopwin-title").html(this.taobaoInfo.nick);
                            }

                            var promoPrice = parseFloat(this.promoPrice);
                            var price = parseFloat(this.price);

                            strListHTML += "<div class=\"win pull-left\">";
                            strListHTML +=      "<div class=\"win-thumbnail\" style=\"background-image:url("+this.thumbnail+");\">";
                            strListHTML +=          "<img src=\"assets/img/imgbg.png\" class=\"win-z\" />";
                             if(promoPrice != price)
                            {
                                var discount = parseInt(promoPrice / price * 10);
                                if(discount<10)
                                {
                                    strListHTML +=          "<span class=\"flag-label\">"+discount+"折</span>";
                                }
                            }
                            strListHTML +=      "</div>";
                            strListHTML +=      "<div class=\"win-info\">";
                            strListHTML +=          "<p class=\"win-title\">"+this.name+"</p>";
                            if(promoPrice != price)
                            {
                                strListHTML +=          "<p class=\"price clearfix\"><em class=\"pull-left\">￥"+promoPrice.toFixed(2)+"</em> <del style=\"margin-left:10pt;\">￥"+price.toFixed(2)+"</del></p>";
                            }
                            else
                            {
                                strListHTML +=          "<p class=\"price clearfix\"><em class=\"pull-left\" style='dis'>￥"+promoPrice.toFixed(2)+"</em> </p>";
                            }
                            strListHTML +=      "</div>";
                            strListHTML += "</div>";
                         }
                    });
                    $("#winItemList").html(strListHTML); 
                    $("div[name='mask']").hide();

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
