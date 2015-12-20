// @formatter:off
define([
], function(
) {
// @formatter:on
    var P03ShareTrade = function(dom, initOptions) {
        P03ShareTrade.superclass.constructor.apply(this, arguments);
        var sharedObject = initOptions.entity;

        var trade = sharedObject.targetInfo.trade;

        var monthArr = ["Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"]
        var tradeSnapshot = trade.tradeSnapshot;

        var tradeItemArr =  new Array();
        var idsArr = new Array()
        idsArr[0] = tradeSnapshot.itemRef;
        idsArr[1] = trade.remix.master.itemRef;
        tradeItemArr[0] = trade.remix.master;
        var _index  = 2;
        if( trade.remix.slaves)
        {
              $.each(trade.remix.slaves, function(index){   
                    if(this.itemRef)
                    {
                        idsArr[_index] = this.itemRef;
                        tradeItemArr[_index-1] = this;
                        _index++;

                    }
               });
        }
        __services.httpService.request('/item/query', 'get', {
          "_ids":idsArr
        }, function(err, metadata, data) {
          if(!err){     
            if(data.items)
            {   
    
                var imgAreaWidth = window.screen.width*0.8;
                var imgAreaHeight = imgAreaWidth * 15 /13;
                var strTopImgAreaHTML = "";
                var strItemHTML = "";
                   $.each(data.items, function(index){    
                         var strActive = "";
                          if(this._id == idsArr[0])
                          { 
                            var strNickName = "--"
                            if(this.shopRef)
                            {
                                strNickName = this.shopRef.nickname 
                            }
                            $('.showcase-title').html("<span>"+ strNickName +"</span>");

                            strActive = "active";
                          }

                          var rectItem = tradeItemArr[0];
                          if(tradeItemArr)
                          {

                            for (var i = tradeItemArr.length - 1; i >= 0; i--) {
                                if(tradeItemArr[i].itemRef == this._id)
                                {
                                    rectItem = tradeItemArr[i];
                                   break;
                                }
                            };

                          }
                        strItemHTML = ""; 
                        strItemHTML += "<div class=\"item-container "+strActive+"\" style=\"background:url("+ this.thumbnail.replace(".jp","_s.jp").replace(".png","_s.png")   +") no-repeat center center / contain;left:"+rectItem.rect[0]+"%; top:"+rectItem.rect[1]+"%; width:"+rectItem.rect[2]+"%; height:"+rectItem.rect[3]+"%;\">";   
                        if(this.expectable && this.expectable.reduction && this.expectable.reduction>0)
                        {
                            strItemHTML += "    <span class=\"flag\">&nbsp;<em>"+this.expectable.reduction+"</em></span></div>"   
                        }

                        strTopImgAreaHTML += strItemHTML;


                     });    
                }

                $('.showcase-container').width(imgAreaWidth);
                $('.showcase-container').height(imgAreaHeight);
                $('.showcase-container').css("margin-left",window.screen.width*0.1);
                $(".showcase-container").html(strTopImgAreaHTML);
          }

        });







        
        //build user's nickName
        __services.httpService.request('/people/query', 'get', {
          "_ids":sharedObject.initiatorRef
        }, function(err, metadata, data) {
          if(!err){
            if(data.peoples)
            {
                currUser = data.peoples[0];
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
            }
          }

        });
        var strHotHTML = "";
        __services.httpService.request('/feeding/hot', 'get', {
          
        }, function(err, metadata, data) {
          if(!err){
            if(data && data.shows)
            {
                var strNickName = "";
                var strportrait = "images/avatar.png";
               $.each(data.shows, function(index){    
                      if(index <=5){
                            strNickName = this.ownerRef.nickname; 
                            // strportrait = this.ownerRef.portrait; 
                            if(this.ownerRef.portrait)
                            {
                                strportrait = this.ownerRef.portrait.replace(".jp","_50.jp").replace(".png","_50.png"); 
                            }


                            strHotHTML +=   "<div class=\"show-item pull-left\">";
                            strHotHTML +=   "<div class=\"thumbnail\">";
                            strHotHTML +=               "<img class=\"show-deep\" src=\""+this.coverForeground.replace(".jp","_s.jp").replace(".png","_s.png")+"\" />";
                            strHotHTML +=               "<div class=\"show-img-container\">"
                            strHotHTML +=                   "<img class=\"show-img\" style=\"width:93%; margin:33% auto auto 4%;\" src=\""+ this.cover.replace(".jp","_s.jp").replace(".png","_s.png") +"\" />";
                            strHotHTML +=               "</div>";
                            strHotHTML +=               "<img class=\"show-imgmask\" src=\""+ this.coverForeground.replace(".jp","_s.jp").replace(".png","_s.png") +"\" />";
                            strHotHTML +=           "</div>";
                            strHotHTML +=           "<div class=\"show-info clearfix\">";
                            strHotHTML +=               "<div class=\"avatar\">";
                            strHotHTML +=                   "<img src=\""+ strportrait  +"\" class=\"avatar-img\" />";

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
            }
          }

           $('#show2').html(strHotHTML);

        });
       
        var search = violet.url.search;
         var currUser;
         __services.httpService.request('/user/loginAsViewer', 'post', {
                
            }, function(err, metadata, data) {
              
                $('.appdown2', this._dom).on('click', __services.downloadService.download);
                $('.download', this._dom).on('click', __services.downloadService.download);
                var strTopImgAreaHTML = "";
                if(err)
                {
                    //print error
                }
                else
                {
                    currUser = data.people;
                    //Bind user info on top navi
                 
                    if(currUser.create)
                    {
                        strCreateData = currUser.create.split("T")[0];

                         var dateStrs = currUser.create.split("-");
                         var month = parseInt(dateStrs[1], 10)-1;
                         var day = parseInt(dateStrs[2], 10);
                        strCreateData = monthArr[month]+"."+day;
                    }
                    $('.date').html(strCreateData);
                   
                     __services.httpService.request('/show/view', 'post', {
                                '_id' : sharedObject._id || ""
                            }, function(err, metadata, data) {
                            });
                }



                $('#show1').html("");
                var varUrPath = currUser.portrait;
                  __services.httpService.request('/feeding/matchCreatedBy', 'get', {
                      "_id":sharedObject.initiatorRef
                    }, function(err, metadata, data) {
                        var strMatchHtml = "";

                        if(!err){

                            if(data && data.shows)
                            {
                                var strNickName = "";
                                var strportrait = "images/avatar.png";
                                $.each(data.shows, function(index){   
                                    if(index <=5){
                                        strNickName = this.ownerRef.nickname; 
                                        if(this.ownerRef.portrait)
                                        {
                                            strportrait = this.ownerRef.portrait.replace(".jp","_50.jp").replace(".png","_50.png"); 
                                        }

                                        strMatchHtml +=   "<div class=\"show-item pull-left\">";
                                        strMatchHtml +=   "<div class=\"thumbnail\">";
                                        strMatchHtml +=               "<img class=\"show-deep\" src=\""+this.coverForeground.replace(".jp","_s.jp").replace(".png","_s.png")+"\" />";
                                        strMatchHtml +=               "<div class=\"show-img-container\">"
                                        strMatchHtml +=                   "<img class=\"show-img\" style=\"width:93%; margin:33% auto auto 4%;\" src=\""+ this.cover.replace(".jp","_s.jp").replace(".png","_s.png")+"\" />";
                                        strMatchHtml +=               "</div>";
                                        strMatchHtml +=               "<img class=\"show-imgmask\" src=\""+ this.coverForeground.replace(".jp","_s.jp").replace(".png","_s.png") +"\" />";
                                        strMatchHtml +=           "</div>";
                                        strMatchHtml +=           "<div class=\"show-info clearfix\">";
                                        strMatchHtml +=               "<div class=\"avatar\">";
                                        strMatchHtml +=                   "<img src=\""+ strportrait +"\" class=\"avatar-img\" />";

                                        if(this.ownerRef.rank && (this.ownerRef.rank ==0 ||this.ownerRef.rank ==1 ) )
                                        {
                                            strMatchHtml +=                   "<span class=\"flag-crown\"></span>";
                                        }
                                        strMatchHtml +=               "</div>"
                                        strMatchHtml +=               "<p class=\"username\">"+strNickName+"</p>"

                                        var dateStrs =this.create.split("-");
                                        var month = parseInt(dateStrs[1], 10)-1;
                                        var day = parseInt(dateStrs[2], 10);
                                        var strTime = monthArr[month]+"."+day;

                                        strMatchHtml +=               "<p class=\"time clearfix\"><i class=\"icon-clock pull-left\"></i><span class=\"pull-left text\">"+ strTime+"</span></p>";
                                        strMatchHtml +=               "<p class=\"hits pull-right\"><i class=\"icon-eye\"></i><span class=\"text\">"+ this.__context.numComments +"</span></p>";
                                        strMatchHtml +=           "</div>";
                                        strMatchHtml +=   "    </div><!-- /.show-item -->";
                                    }
                                 });    
                            }
                        }

                      $('#show1').html(strMatchHtml);
                    });  


            });
    };
    violet.oo.extend(P03ShareTrade, violet.ui.ViewBase);


    P03ShareTrade.prototype._resizeHandler = function() {
        $('.p03-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P03ShareTrade;
});
