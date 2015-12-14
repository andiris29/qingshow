// @formatter:off
define([
], function(
) {
// @formatter:on
    var P03ShareTrade = function(dom, initOptions) {
        P03ShareTrade.superclass.constructor.apply(this, arguments);

        // var imageArray = [];
        // imageArray.push(__config.image.root + "/assets/slicing/p02/share_bonu_bg.png");
        // imageArray.push(__config.image.root + "/assets/slicing/common/a1.jpg");
        // imageArray.push(__config.image.root + "/assets/slicing/common/a3.jpg");
        // imageArray.push(__config.image.root + "/assets/slicing/common/a4.jpg");

        // var $doms = $('.p03-image-slider-block-image', this._dom);
        // for (var index = 1; index < $doms.size(); index++) {
        //     var dom = $doms[index];
        //     $(dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        // }
        // $('.p03-trade-item-actual-price-content', this._dom).css('background-image', violet.string.substitute('url({0})', __config.image.root + '/assets/slicing/p03/p03_trade_item_actual_price_container_bg.png'));

        // $(window).resize( function() {
        //     //TODO workaround
        //     setTimeout(function (){
        //         this._resizeHandler();
        //     }.bind(this), 100);

        // }.bind(this));



        // $('.p03-image-slider-block-content', this._dom).hide();
        // $('.p03-download', this._dom).on('click', __services.downloadService.download);


        var trade = initOptions.entity.trade;   
        var show = initOptions.entity.show;
        var tradeMaster;
        if(trade)
        {
            tradeMaster = trade.remix.master;
        }


         __services.httpService.request('/feeding/hot', 'get', {
          
        }, function(err, metadata, data) {
          if(!err){

                //绑定item数据
          }
          else
          {
             $("#show2").html();
          }

        });

        var search = violet.url.search;
         var currUser;
         __services.httpService.request('/user/loginAsViewer', 'post', {
              
            }, function(err, metadata, data) {

                if(show)
                {
                    //bind data
                    if(show.showSnapshot && show.showSnapshot.cover)
                    {
                        $(".share-img").attr("src",show.showSnapshot.cover);            
                    }
                    if(show.showSnapshot && show.showSnapshot.coverForeground)
                    {
                        $(".share-imgmask").attr("src",show.showSnapshot.coverForeground);            
                    }
                      var screenHeight = window.screen.height
                    $(".share-imagearea").height(screenHeight-40);
                    $(".share-img").css("margin-top",110);  
                }
                $('.appdown2', this._dom).on('click', __services.downloadService.download);
                $('.download', this._dom).on('click', __services.downloadService.download);
                if(tradeMaster)
                {
                     if(tradeMaster.itemSnapshot)
                     {
                        $('.showcase-title').html("<span>"+tradeMaster.itemSnapshot.name+"</span>");
                     }
                }
                if(err)
                {
                    //print error
                }
                else
                {
                    currUser = data.people;
                    //Bind user info on top navi
                    var strNickName = "--";
                    var strCreateData = " ";
                    if(currUser.nickname)
                    {
                        strNickName = currUser.nickname;
                    }
                    $('.username').html(strNickName);
                    $('.navtab1').html(strNickName+"的其它美搭");
                    if(currUser.create)
                    {
                        strCreateData = currUser.create.split("T")[0];
                    }
                    $('.date').html(strCreateData);
                     if(currUser.portrait)
                    {
                        $("#portrait").attr("src",currUser.portrait);
                    }
                    else
                    {
                        $("#portrait").attr("src","images/avatar.png");
                    }
                 
                }
            });



        // var originPrice = parseFloat(trade.itemSnapshot.promoPrice).toFixed(2);
        // var actualPrice = (parseFloat(trade.totalFee) / parseFloat(trade.quantity)).toFixed(2);
        // var discount = parseInt(actualPrice * 10 / originPrice + 0.5);
        // discount = discount < 1 ? 1 : discount;
        // discount = discount > 9 ? 9 : discount;

        // $('.p03-trade-item-title', this._dom)[0].innerText = trade.itemSnapshot.name;
        // $('.p03-trade-item-price-number', this._dom)[0].innerText = '￥' + originPrice;
        // $('.p03-trade-item-actual-price-number', this._dom)[0].innerText = '￥' + actualPrice;
        // $('.p03-trade-item-discount-number', this._dom)[0].innerText = discount + "折";
        // $('.p03-trade-item-tmall-price-block', this._dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        // $('.p03-trade-item-tmall-price-block', this._dom).css('background-image', violet.string.substitute('url({0})', trade.itemSnapshot.thumbnail));

        // setTimeout(function() {
        //     $('.p03-image-slider', this._dom).slick({
        //         'infinite' : true,
        //         'centerMode' : true,
        //         'slidesToShow' : 1,
        //         'centerPadding' : '15%'
        //     });
        //     $('.p03-image-slider-block-content', this._dom).show();

        //     $('.slick-center', this._dom).resize(function () {
        //         console.log('eeeaaa');
        //     });

        //     this._resizeHandler();
        // }.bind(this), 0);
    };
    violet.oo.extend(P03ShareTrade, violet.ui.ViewBase);


    P03ShareTrade.prototype._resizeHandler = function() {
        $('.p03-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P03ShareTrade;
});
