// @formatter:off
define([    'qs/services/httpService',
    'qs/services/navigationService',
    'qs/services/downloadService',
    'qs/core/model',
    'require'
], function(
    ) {
// @formatter:on
    var P02ShareShow = function(dom, initOptions) {
        P02ShareShow.superclass.constructor.apply(this, arguments);
  
        var show = initOptions.entity;
        var navigationService = initOptions.navigationService;
        var monthArr = ["Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"]
        var showid = "";
         if(show && show.showRef)
         {
            showid = show.showRef
         }
         else if(show && show._id)
         {
            showid = show._id
         }
         bindTabClickEvent();
        var search = violet.url.search;
         __services.httpService.request('/user/loginAsViewer', 'post', {
            }, function(err, metadata, data) {});

        
         if(showid != "")
         {
             __services.httpService.request('/show/view', 'post', {
                    '_id' : show.showRef || ""
                }, function(err, metadata, data) {});
         }

         //build user's nickName
        __services.httpService.request('/show/query', 'get', {
          "_ids":showid
        }, function(err, metadata, data) {
          if(!err){
            if(data.shows)
            {
                //=======================begin bind basic info=======================
                var trueShowItem = data.shows[0];
                var currUser = trueShowItem.ownerRef;
                var strNickName = "--";
                var strCreateData = " ";
                if(currUser.nickname)
                {
                    strNickName = currUser.nickname;
                }
                $('.username').html(strNickName);
                $('#navtab1').html(strNickName+"的其它美搭");
                var strportrait = "images/avatar.png";
                if(currUser.portrait)
                {
                    strportrait = currUser.portrait.replace(".jp","_50.jp").replace(".png","_50.png");
                }
                $("#portrait").attr("src",strportrait);
                if(currUser.create)
                {
                    strCreateData = currUser.create.split("T")[0];
                     var dateStrs = currUser.create.split("-");
                     var month = parseInt(dateStrs[1], 10)-1;
                     var day = parseInt(dateStrs[2], 10);
                    strCreateData = monthArr[month]+"."+day;
                }
                $('.date').html(strCreateData);


                if(trueShowItem && trueShowItem.cover)
                {
                    $(".share-img").attr("src",trueShowItem.cover.replace(".png","_s.png"));            
                }
                if(trueShowItem && trueShowItem.coverForeground)
                {
                    $(".share-imgmask").attr("src",trueShowItem.coverForeground.replace(".png","_s.png"));   
                    $(".share-deep").attr("src",trueShowItem.coverForeground.replace(".png","_s.png"));
                }

                $('.appdown2', this._dom).on('click', __services.downloadService.download);
                $('.download', this._dom).on('click', __services.downloadService.download);

                var strTagHTML = "";
                var strItemHTML = "";
                if(trueShowItem.itemRects)
                {
                     $.each(trueShowItem.itemRects, function(index){  
                         if(!trueShowItem.itemRefs[index].delist)
                         {
                            strItemHTML  = "";
                            strItemHTML += " <div class=\"share-item-box\"  id=\""+trueShowItem._id+"\"   style=\"left:"+ this[0] +"%; top:"+ this[1] +"%;width:"+ this[2] +"%; height:"+ this[3] +"%; \">"
                            strItemHTML +=    "<span class=\"flag\"  >&nbsp;<em>"+ trueShowItem.itemRefs[index].expectable.reduction +"</em></span>";
                            strItemHTML += "</div>";
                            strTagHTML += strItemHTML;
                        }
                     });

                    
                }
               strTagHTML =  "<img class=\"share-img\" style=\"width:100%;\" src=\""+trueShowItem.cover.replace(".png","_s.png")+"\" />" + strTagHTML;
                $(".img-mask").html(strTagHTML);

                 $(".share-item-box").unbind("click");
                 $('.share-item-box').on('click', function(){ 
                    var  _id = this.id;

                    __services.navigationService.push('qs/views/P05ShareShow', {
                                        'items' : _id,
                                    });
                    // __services.httpService.request('matcher/queryShopItems', 'get', {
                    //   "itemRef":_id,
                    //   "pageNo":1,
                    //   "pageSize":10
                    // }, function(err, metadata, data) {
                    //     if(!err)
                    //     {
                    //         if(data && data.items)
                    //         {
                    //                navigationService.push('qs/views/P05ShareShow', {
                    //                     'items' : data.items,
                    //                 });
                    //         }
                    //         else
                    //         {
                    //             navigationService.push('qs/views/P01NotFound');
                    //         }
                    //     }
                    // });
                });

                
                /*bind  createby begin*/
                $('#show1').html("");
                var varUrPath = currUser.portrait;
                  __services.httpService.request('/feeding/matchCreatedBy', 'get', {
                      "_id":currUser._id
                    }, function(err, metadata, data) {
                        if(!err) {
                            if(data && data.shows)
                            {   
                                bindShowList("show1",data.shows,navigationService,monthArr);   
                            }
                        }
                });
                /*bind  createby end*/

                /*bind hot begin*/
                $('#show2').html("");
                __services.httpService.request('/feeding/hot', 'get', {
                  
                }, function(err, metadata, data) {
                  if(!err){
                    if(data && data.shows)
                    {
                        bindShowList("show2",data.shows,navigationService,monthArr);
                    }
                  }
                });


                /*bind hot end*/
            }
          }
        });
    };

    function bindShowList(panelID,showList,navigationService,monthArr)
    {
        var strNickName = "";
        var strportrait = "images/avatar.png";
        var strHotHTML = "";
         $.each(showList, function(index){    
              if(index <=5){
                    strNickName = this.ownerRef.nickname; 
                    if(this.ownerRef.portrait)
                    {
                        strportrait = this.ownerRef.portrait;
                        strportrait = strportrait.replace(".jp","_50.jp").replace(".png","_50.png");
                    } 
                    strHotHTML +=   "<div class=\"show-item pull-left\">";
                    strHotHTML +=   "<div class=\"thumbnail\"  id='"+ this._id +"'>";
                    strHotHTML +=               "<img class=\"show-deep\" src=\""+this.coverForeground.replace(".jp","_s.jp").replace(".png","_s.png")+"\" />";
                    strHotHTML +=               "<div class=\"show-img-container\">"
                    strHotHTML +=                   "<img class=\"show-img\" style=\"width:93%; margin:33% auto auto 4%;\" src=\""+ this.cover.replace(".jp","_s.jp").replace(".png","_s.png") +"\" />";
                    strHotHTML +=               "</div>";
                    strHotHTML +=               "<img class=\"show-imgmask\" src=\""+ this.coverForeground.replace(".jp","_s.jp").replace(".png","_s.png") +"\" />";
                    strHotHTML +=           "</div>";
                    strHotHTML +=           "<div class=\"show-info clearfix\">";
                    strHotHTML +=               "<div class=\"avatar\">";
                    strHotHTML +=                   "<img src=\""+ strportrait +"\" class=\"avatar-img\" />";

                    if(this.ownerRef.rank && (this.ownerRef.rank ==0 ||this.ownerRef.rank ==1 ) )
                    {
                        strHotHTML +=                   "<span class=\"flag-crown\"></span>";
                    }
                    strHotHTML +=               "</div>"
                    strHotHTML +=               "<p class=\"username\">"+strNickName+"</p>"
                   
                    var dateStrs =this.create.split("-");
                    var month = parseInt(dateStrs[1], 10)-1;
                    var day = parseInt(dateStrs[2], 10);
                    var strTime = monthArr[month]+"."+day;
                    

                    strHotHTML +=               "<p class=\"time clearfix\"><i class=\"icon-clock pull-left\"></i><span class=\"pull-left text\">"+ strTime +"</span></p>";
                    strHotHTML +=               "<p class=\"hits pull-right\"><i class=\"icon-eye\"></i><span class=\"text\">"+ this.__context.numComments +"</span></p>";
                    strHotHTML +=           "</div>";
                    strHotHTML +=   "    </div><!-- /.show-item -->";
                }
         });  
         var listID =  '#'+panelID
         $(listID).html(strHotHTML);  

         bindTabClickEvent();

        //bind Click
        $('.thumbnail').unbind("click"); 
        $('.thumbnail').on('click', function(){ 
            var  _id = this.id;
            __services.httpService.request('/show/query', 'get', {
              "_ids":[_id]
            }, function(err, metadata, data) {
                    if(data.shows)
                    {
                        var currShow =  data.shows[0];
                           __services.navigationService.push('qs/views/P02ShareShow', {
                                'entity' : currShow
                            });
                    }
                    else
                    {
                        __services.navigationService.push('qs/views/P01NotFound');
                    }
                });
        });
    }

    function bindTabClickEvent()
    {
        //bind Click
        $('#navtab1').unbind("click"); 
        $('#navtab1').on('click', function(){ 
            $('#show1').css("display","block");
            $('#show2').css("display","none");

            $('#navtab1').attr("class", "navtab-item navtab-x2 active pull-left"); 
            $('#navtab2').attr("class", "navtab-item navtab-x2 pull-left"); 

     
        });

        $('#navtab2').unbind("click"); 
        $('#navtab2').on('click', function(){ 
            $('#show2').css("display","block");
            $('#show1').css("display","none");

            $('#navtab2').attr("class", "navtab-item navtab-x2 active pull-left"); 
            $('#navtab1').attr("class", "navtab-item navtab-x2 pull-left"); 


        });
    }

    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    P02ShareShow.prototype._resizeHandler = function() {
        $('.p02-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P02ShareShow;
});
