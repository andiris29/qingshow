// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P02ShareShow = function(dom, initOptions) {
        P02ShareShow.superclass.constructor.apply(this, arguments);
        var show = initOptions.entity.show;
        var trade = initOptions.entity.trade;
        
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
                "invitorRef":show.showSnapshot.ownerRef
            }, function(err, metadata, data) {

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

                $('.appdown2', this._dom).on('click', __services.downloadService.download);
                $('.download', this._dom).on('click', __services.downloadService.download);



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
                     __services.httpService.request('/show/view', 'post', {
                                '_id' : show.showSnapshot._id || ""
                            }, function(err, metadata, data) {
                            });
                }
            });



      

      
       

        // var imageArray = [''];
        // imageArray.push(__config.image.root + "/assets/slicing/common/a3.jpg");
        // imageArray.push(__config.image.root + "/assets/slicing/common/a4.jpg");
        // imageArray.push(__config.image.root + "/assets/slicing/common/a1.jpg");
        // imageArray.push(__config.image.root + "/assets/slicing/common/a2.jpg");

        // var $doms = $('.p02-image-slider-block-image', this._dom);
        // for (var index = 1; index < $doms.size(); index++) {
        //     var dom = $doms[index];
        //     $(dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        // }

        
        // var $dom = $($('.p02-image-slider-block-image', this._dom)[0]);
        // $dom.css('background-image', violet.string.substitute('url({0})', show.cover));
        // $dom.attr('src', show.coverForeground);

        // $(window).resize( function() {
        //     //TODO workaround
        //     setTimeout(function (){
        //         this._resizeHandler();
        //     }.bind(this), 100);
        // }.bind(this));

        // $('.p02-image-slider-block-content', this._dom).hide();
        // $('.p02-download', this._dom).on('click', __services.downloadService.download);

        // setTimeout(function () {
        //     $('.p02-image-slider', this._dom).slick({
        //         'infinite' : true,
        //         'centerMode' : true,
        //         'slidesToShow' : 1,
        //         'centerPadding' : '15%'
        //     });
        //     $('.p02-image-slider-block-content', this._dom).show();
        //     this._resizeHandler();
        // }.bind(this), 0);
    };

    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    P02ShareShow.prototype._resizeHandler = function() {
        $('.p02-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P02ShareShow;
});
