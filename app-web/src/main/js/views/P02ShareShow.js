// @formatter:off
define([ 
    ], function(
        ) {
// @formatter:on
var P02ShareShow = function(dom, initOptions) {
    P02ShareShow.superclass.constructor.apply(this, arguments);

    var show = initOptions.entity;
   
    var showid = "";
    if(show && show.showRef)
    {
        showid = show.showRef
    }
    else if(show && show._id)
    {
        showid = show._id
    }
    pageLoadCall(showid);
    //build user's nickName
    __services.httpService.request('/show/query', 'get', {
      "_ids":showid
    }, function(err, metadata, data) {
          if(!err){
            if(data.shows)
            {

                bindTappClickEvent.call(this, showid);
                bindFirstScreen.call(this, data);


                this.$('#show1').html("");
                __services.httpService.request('/feeding/matchCreatedBy', 'get', {
                  "_id":data.shows[0].ownerRef._id
              }, function(err, metadata, data) {
                if(!err) {
                    if(data && data.shows)
                    {   
                        bindShowList.call(this, "show1",data.shows);   
                    }
                }
            }.bind(this));


                this.$('#show2').html("");
                __services.httpService.request('/feeding/hot', 'get', {

                }, function(err, metadata, data) {
                    if(!err){
                        if(data && data.shows)
                        {
                            bindShowList.call(this, "show2",data.shows);
                        }
                    }
                }.bind(this));

            }
        }
    }.bind(this));
};

function bindShowList(panelID,showList)
{
     var monthArr = ["Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"]
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
this.$(listID).html(strHotHTML);  
this.$('.thumbnail').unbind("click"); 
this.$('.thumbnail').on('click', function(){ 
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

function bindFirstScreen(data)
{
    var trueShowItem = data.shows[0];
    var currUser = trueShowItem.ownerRef;
    var strNickName = "--";
    var strCreateData = " ";
    if(currUser.nickname)
    {
        strNickName = currUser.nickname;
    }
    this.$('.username').html(strNickName);
    this.$('#navtab1').html(strNickName+"的其它美搭");
    var strportrait = "images/avatar.png";
    if(currUser.portrait)
    {
        strportrait = currUser.portrait.replace(".jp","_50.jp").replace(".png","_50.png");
    }
    this.$("#portrait").attr("src",strportrait);
        // if(currUser.create)
        // {
        //     strCreateData = currUser.create.split("T")[0];
        //      var dateStrs = currUser.create.split("-");
        //      var month = parseInt(dateStrs[1], 10)-1;
        //      var day = parseInt(dateStrs[2], 10);
        //     strCreateData = monthArr[month]+"."+day;
        // }
        // this.$('.date').html(strCreateData);

        if(trueShowItem && trueShowItem.cover)
        {
            this.$(".share-img").attr("src",trueShowItem.cover.replace(".png","_s.png"));            
        }
        if(trueShowItem && trueShowItem.coverForeground)
        {
            this.$(".share-imgmask").attr("src",trueShowItem.coverForeground.replace(".png","_s.png"));   
            this.$(".share-deep").attr("src",trueShowItem.coverForeground.replace(".png","_s.png"));
        }

        var strTagHTML = "";
        var strItemHTML = "";
        if(trueShowItem.itemRects)
        {
         $.each(trueShowItem.itemRects, function(index){  
             if(!trueShowItem.itemRefs[index].delist)
             {
                strItemHTML  = "";
                strItemHTML += " <div class=\"share-item-box\"  id=\""+trueShowItem.itemRefs[index]._id+"\"   style=\"left:"+ this[0] +"%; top:"+ this[1] +"%;width:"+ this[2] +"%; height:"+ this[3] +"%; \">"
                strItemHTML +=    "<span class=\"flag\"  >&nbsp;<em>"+ trueShowItem.itemRefs[index].expectable.reduction +"</em></span>";
                strItemHTML += "</div>";
                strTagHTML += strItemHTML;
            }
        });


     }
     strTagHTML =  "<img class=\"share-img\" style=\"width:100%;\" src=\""+trueShowItem.cover.replace(".png","_s.png")+"\" />" + strTagHTML;
     this.$(".img-mask").html(strTagHTML);
     this.$(".share-item-box").unbind("click");
     this.$('.share-item-box').on('click', function(){ 
        var  _id = this.id;

        // var url = window.location.href.replace(window.location.search,"");
        // url += ("?_itemid="+_id)
        // window.location.href=url



        __services.navigationService.push('qs/views/P05ShareItems', {
            '_itemid' : _id,
        });

        // __services.httpService.request('/matcher/queryShopItems', 'get', {
        //   "itemRef":_id,
        //   "pageNo":1,
        //   "pageSize":10
        //   }, function(err, metadata, data) {
        //     if(!err)
        //     {
        //         if(data && data.items)
        //         {

        //             __services.navigationService.push('qs/views/P05ShareItems', {
        //                 'items' : data.items,
        //             });
        //         }
        //         else
        //         {
        //             __services.navigationService.push('qs/views/P01NotFound');
        //         }
        //     }
        // });


    });
 }

 function bindTappClickEvent(showid)
 {
        //bind Click
        this.$('#navtab1').unbind("click"); 
        this.$('#navtab1').on('click', function(){ 
            this.$('#show1').css("display","block");
            this.$('#show2').css("display","none");

            this.$('#navtab1').attr("class", "navtab-item navtab-x2 active pull-left"); 
            this.$('#navtab2').attr("class", "navtab-item navtab-x2 pull-left"); 


        });

        this.$('#navtab2').unbind("click"); 
        this.$('#navtab2').on('click', function(){ 
            this.$('#show2').css("display","block");
            this.$('#show1').css("display","none");

            this.$('#navtab2').attr("class", "navtab-item navtab-x2 active pull-left"); 
            this.$('#navtab1').attr("class", "navtab-item navtab-x2 pull-left"); 


        });

        this.$('.appdown2', this._dom).on('click', __services.downloadService.download);
        this.$('.download', this._dom).on('click', __services.downloadService.download);

        this.$('.face-like').unbind("click"); 
        this.$('.face-like').on('click', function(){ 
            __services.httpService.request('/share/like', 'post', {
              "_id":showid
          }, function(err, metadata, data) {
            this.$('.dialog-box').show()

            
            if(!err && data)
            {


            }
        });
        });

        this.$('.face-unlike').unbind("click"); 
        this.$('.face-unlike').on('click', function(){ 
            __services.httpService.request('/share/unlike', 'post', {
              "_id":showid
          }, function(err, metadata, data) {
             this.$('.dialog-box').show()
            if(!err && data)
            {

            }
        });
        });
    }

    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    P02ShareShow.prototype._resizeHandler = function() {
        this.$('.p02-image-slider-block-image', this._dom).css({
            'height' : this.$('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P02ShareShow;
});
