// @formatter:off
define([
], function(
) {
// @formatter:on
    var P03ShareTrade = function(dom, initOptions) {
        P03ShareTrade.superclass.constructor.apply(this, arguments);
        var sharedObject = initOptions.entity;

        var trade = sharedObject.targetInfo.trade;

        
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
                    strportrait = currUser.portrait;
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
                var strportrait = "";
               $.each(data.shows, function(index){    
                      if(index <=5){
                            strNickName = this.ownerRef.nickname; 
                            strportrait = this.ownerRef.portrait; 

                            strHotHTML +=   "<div class=\"show-item pull-left\">";
                            strHotHTML +=   "<div class=\"thumbnail\">";
                            strHotHTML +=               "<img class=\"show-deep\" src=\""+this.coverForeground.replace(".","_s.")+"\" />";
                            strHotHTML +=               "<div class=\"show-img-container\">"
                            strHotHTML +=                   "<img class=\"show-img\" style=\"width:93%; margin:33% auto auto 4%;\" src=\""+ this.cover.replace(".","_s.") +"\" />";
                            strHotHTML +=               "</div>";
                            strHotHTML +=               "<img class=\"show-imgmask\" src=\""+ this.coverForeground.replace(".","_s.") +"\" />";
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
                            strHotHTML +=               "<p class=\"time clearfix\"><i class=\"icon-clock pull-left\"></i><span class=\"pull-left text\">"+ this.create.replace("T"," ")+"</span></p>";
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

                //bind data
                $('.showcase-title').html("<span>"+ sharedObject.title +"</span>");
                $('.appdown2', this._dom).on('click', __services.downloadService.download);
                $('.download', this._dom).on('click', __services.downloadService.download);
                var strTopImgAreaHTML = "";
                if(trade && trade.remix)
                {
                    var itemMaster =  trade.remix.master;
                    var slavesArr =  trade.remix.slaves;
                    var strItemHTML = "";

                var imgAreaWidth = window.screen.width*0.8;
                var imgAreaHeight = imgAreaWidth * 15 /13;

                    if(itemMaster)
                    {
                        var leftMargin =  itemMaster.rect[0];
                        var topMargin =  itemMaster.rect[1];
                        var itemWidth =  itemMaster.rect[2];
                        var itemHeight =  itemMaster.rect[3];

                        strItemHTML = ""; 
                        strItemHTML += "<div class=\"item-container\" style=\"background:url("+ itemMaster.itemSnapshot.thumbnail  +") no-repeat center center / contain;left:"+itemMaster.rect[0]+"%; top:"+itemMaster.rect[1]+"%; width:"+itemMaster.rect[2]+"%; height:"+itemMaster.rect[3]+"%;\">";   
                        strItemHTML += "    <span class=\"flag\">立减<em>"+itemMaster.itemSnapshot.expectable.reduction+"</em></span></div>"
                        strTopImgAreaHTML += strItemHTML;
                    }
                }
                $.each(trade.remix.slaves,function(index){
                    strItemHTML = "";
                    if(this)
                    {
                        strItemHTML = ""; 
                        strItemHTML += "<div class=\"item-container\" style=\"background:url("+ this.itemSnapshot.thumbnail  +") no-repeat center center / contain;left:"+this.rect[0]+"%; top:"+this.rect[1]+"%; width:"+this.rect[2]+"%; height:"+this.rect[3]+"%;\">";   
                        if(this.itemSnapshot.expectable && this.itemSnapshot.expectable.reduction && this.itemSnapshot.expectable.reduction>0)
                        {
                            strItemHTML += "    <span class=\"flag\">立减<em>"+this.itemSnapshot.expectable.reduction+"</em></span></div>"   
                        }

                        strTopImgAreaHTML += strItemHTML;
                    }
                });
                $('.showcase-container').width(imgAreaWidth);
                $('.showcase-container').height(imgAreaHeight);
                $('.showcase-container').css("margin-left",window.screen.width*0.1);
                $(".showcase-container").html(strTopImgAreaHTML);
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
                                var strportrait = "";
                                $.each(data.shows, function(index){   
                                    if(index <=5){
                                        strNickName = this.ownerRef.nickname; 
                                        strportrait = this.ownerRef.portrait; 

                                        strMatchHtml +=   "<div class=\"show-item pull-left\">";
                                        strMatchHtml +=   "<div class=\"thumbnail\">";
                                        strMatchHtml +=               "<img class=\"show-deep\" src=\""+this.coverForeground.replace(".","_s.")+"\" />";
                                        strMatchHtml +=               "<div class=\"show-img-container\">"
                                        strMatchHtml +=                   "<img class=\"show-img\" style=\"width:93%; margin:33% auto auto 4%;\" src=\""+ this.cover.replace(".","_s.") +"\" />";
                                        strMatchHtml +=               "</div>";
                                        strMatchHtml +=               "<img class=\"show-imgmask\" src=\""+ this.coverForeground.replace(".","_s.") +"\" />";
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
                                        strMatchHtml +=               "<p class=\"time clearfix\"><i class=\"icon-clock pull-left\"></i><span class=\"pull-left text\">"+ this.create.replace("T"," ")+"</span></p>";
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
