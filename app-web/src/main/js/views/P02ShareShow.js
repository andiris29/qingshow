// @formatter:off
define([],
function() {
    // @formatter:on
    var P02ShareShow = function(dom, initOptions) {
        P02ShareShow.superclass.constructor.apply(this, arguments);
        // var shareObj = initOptions.entity;
        var show = initOptions.entity;
        var shareObj;
        if(initOptions.paraObj)
        {
            shareObj = initOptions.paraObj;
        }
        var showid = "";
        if (show && show.showRef) {
            showid = show.showRef
        }
         else if (show && show._id) {
            showid = show._id
        }
        pageLoadCall(showid);
        __services.httpService.request('/show/query', 'get', {
            "_ids": showid
        },

        function(err, metadata, data) {
            if (!err) {
                if (data.shows) {

                     var totalVoteCount = "";
                    if (shareObj) {
                        totalVoteCount = shareObj.numDislike + shareObj.numLike;
                        this.$(".totalVoteCount").html(totalVoteCount);
                    }
                    else
                    {
                        $(".navbar-right").hide();
                        $(".vote-options").hide();
                    }

                    bindTappClickEvent.call(this, showid,shareObj);
                    bindFirstScreen.call(this, data);

                    
                    __services.httpService.request('/people/query', 'get', {
                        '_ids': [data.shows[0].ownerRef._id]
                    },function(err, metadata, data) {
                        if(!err)
                        {

                            var inComeTotal = 0;
                            var incomeArr = data.peoples[0].__context.bonusAmountByStatus;
                            if(incomeArr)
                            {
                                $.each(incomeArr,function(index){
                                    if(index < 2)
                                    {
                                        inComeTotal += this;
                                    }
                                });
                                $(".income").html("<p>收益￥"+inComeTotal.toFixed(2)+"</p>");
                            }
                            else
                            {
                                $(".income").html("<p>收益￥"+inComeTotal.toFixed(2)+"</p>");
                            }
                        }
                        else
                        {
                            $(".income").hide();
                        }
                    });
                

                    this.$('#show1').html("");
                    __services.httpService.request('/feeding/matchCreatedBy', 'get', {
                        "_id": data.shows[0].ownerRef._id
                    },function(err, metadata, data) {
                        if (!err) {
                            if (data && data.shows) {
                                bindShowList.call(this, "show1", data.shows);
                            }
                        }
                    }.bind(this));

                    this.$('#show2').html("");
                    __services.httpService.request('/feeding/hot', 'get', {
                        'from': GetDateStr( - 1),
                        'to': GetDateStr(0)

                    },function(err, metadata, data) {
                        if (!err) {
                            if (data && data.shows) {
                                bindShowList.call(this, "show2", data.shows);
                            }
                        }
                    }.bind(this));

                }
            }
        }.bind(this));
    };

    function GetDateStr(dayCountDiff) {
        var dd = new Date();
        dd.setDate(dd.getDate() + dayCountDiff); //获取dayCountDiff天后的日期 
        var y = dd.getFullYear();
        var m = dd.getMonth() + 1; //获取当前月份的日期 
        var d = dd.getDate();
        return y + "-" + m + "-" + d;
    }
    function bindShowList(panelID, showList) {
        var monthArr = ["Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"]
        var strNickName = "";
        var strportrait = "img/avatar.png";
        var strHotHTML = "";
        $.each(showList,
        function(index) {
            if (index <= 5) {
                strNickName = this.ownerRef.nickname;
                if (this.ownerRef.portrait) {
                    strportrait = this.ownerRef.portrait;
                    strportrait = strportrait.replace(".jp", "_50.jp").replace(".png", "_50.png");
                }
                strHotHTML += "<div class=\"show-item pull-left\">";
                strHotHTML += "<div class=\"thumbnail\"  id='" + this._id + "'>";
                strHotHTML += "<img class=\"show-deep\" src=\"" + this.coverForeground.replace(".jp", "_s.jp").replace(".png", "_s.png") + "\" />";
                strHotHTML += "<div class=\"show-img-container\">";
                strHotHTML += "<img class=\"show-img\" style=\"width:93%; margin:33% auto auto 4%;\" src=\"" + this.cover.replace(".jp", "_s.jp").replace(".png", "_s.png") + "\" />";
                strHotHTML += "</div>";
                strHotHTML += "<img class=\"show-imgmask\" src=\"" + this.coverForeground.replace(".jp", "_s.jp").replace(".png", "_s.png") + "\" />";
                strHotHTML += "</div>";
                strHotHTML += "<div class=\"show-info clearfix\">";
                strHotHTML += "<div class=\"avatar\">";
                strHotHTML += "<img src=\"" + strportrait + "\" class=\"avatar-img\" />";

                if (this.ownerRef.rank) {
                    if(this.ownerRef.rank == 0)
                    {//金冠
                        strHotHTML += "<span class=\"flag-crown\"></span>";
                    }
                    else if(this.ownerRef.rank == 1)
                    {//银冠
                        strHotHTML += "<span class=\"flag-sliver\"></span>";
                    }
                }
                strHotHTML += "</div>";
                strHotHTML += "<p class=\"username\">" + strNickName + "</p>";

                var dateStrs = this.create.split("-");
                var month = parseInt(dateStrs[1], 10) - 1;
                var day = parseInt(dateStrs[2], 10);
                var strTime = monthArr[month] + "." + day;

                strHotHTML += "<p class=\"time clearfix\"><i class=\"icon-clock pull-left\"></i><span class=\"pull-left text\">" + strTime + "</span></p>";
                strHotHTML += "<p class=\"hits pull-right\"><i class=\"icon-eye\"></i><span class=\"text\">" + this.__context.numComments + "</span></p>";
                strHotHTML += "</div>";
                strHotHTML += "    </div><!-- /.show-item -->";
            }
        });
        var listID = '#' + panelID;
        this.$(listID).html(strHotHTML);
        this.$('.thumbnail').unbind("click");
        this.$('.thumbnail').on('click',
        function() {
            var _id = this.id;
            __services.httpService.request('/show/query', 'get', {
                "_ids": [_id]
            },
            function(err, metadata, data) {
                if (data.shows) {
                    var currShow = data.shows[0];
                    __services.navigationService.push('qs/views/P02ShareShow', {
                        'entity': currShow
                    });
                } else {
                    __services.navigationService.push('qs/views/P01NotFound');
                }
            });
        });

    }
    function pageLoadCall(showid) {
        var search = violet.url.search;
        __services.httpService.request('/user/loginAsViewer', 'post', {},
        function(err, metadata, data) {});

        if (showid != "") {
            __services.httpService.request('/show/view', 'post', {
                '_id': showid
            },
            function(err, metadata, data) {});
        }
    }

    function bindFirstScreen(data) {

        var trueShowItem = data.shows[0];

  

        var currUser = trueShowItem.ownerRef;
        var strNickName = "--";
        var strCreateData = " ";
        if (currUser.nickname) {
            strNickName = currUser.nickname;
        }
        this.$('.username').html(strNickName);
        this.$('#navtab1').html(strNickName + "的其它美搭");
        var strportrait = "img/avatar.png";
        if (currUser.portrait) {
            strportrait = currUser.portrait.replace(".jp", "_50.jp").replace(".png", "_50.png");
        }
        // this.$(".avatar").attr("src", strportrait);
         $("img[name='portrait']").attr("src", strportrait);
        if (trueShowItem && trueShowItem.cover) {
            this.$(".share-img").attr("src", trueShowItem.cover.replace(".png", "_s.png"));
        }
        if (trueShowItem && trueShowItem.coverForeground) {
            this.$(".share-imgmask").attr("src", trueShowItem.coverForeground.replace(".png", "_s.png"));
            this.$(".share-deep").attr("src", trueShowItem.coverForeground.replace(".png", "_s.png"));
        }

        var strTagHTML = "";
        var strItemHTML = "";
        if (trueShowItem.itemRects) {
            $.each(trueShowItem.itemRects,
            function(index) {
                if (!trueShowItem.itemRefs[index].delist) {
                    strItemHTML = "";
                    strItemHTML += " <div class=\"share-item-box\"  id=\"" + trueShowItem.itemRefs[index]._id + "\"   style=\"left:" + this[0] + "%; top:" + this[1] + "%;width:" + this[2] + "%; height:" + this[3] + "%; \">";
                    strItemHTML += "<span class=\"flag\"  >&nbsp;<em>" + trueShowItem.itemRefs[index].expectable.reduction + "</em></span>";
                    strItemHTML += "</div>";
                    strTagHTML += strItemHTML;
                }
            });

        }
        strTagHTML = "<img class=\"share-img\" style=\"width:100%;\" src=\"" + trueShowItem.cover.replace(".png", "_s.png") + "\" />" + strTagHTML;
        this.$(".img-mask").html(strTagHTML);
        this.$(".share-item-box").unbind("click");
        this.$('.share-item-box').on('click',
        function() {
            var _id = this.id;
            __services.navigationService.push('qs/views/P05ShareItems', {
                '_itemid': _id,
            });
        });
    }

    function bindTappClickEvent(showid,shareObj) {

        this.$("div[name='navtab1']").on('click',
        function() {
            $("div[name='show1']").css("display", "block");
            $("div[name='show2']").css("display", "none");

            $("div[name='navtab1']").attr("class", "navtab-item navtab-x2 active pull-left");
            $("div[name='navtab2']").attr("class", "navtab-item navtab-x2 pull-left");

        });

        this.$("div[name='navtab2']").on('click',
        function() {
            $("div[name='show2']").css("display", "block");
            $("div[name='show1']").css("display", "none");

            $("div[name='navtab2']").attr("class", "navtab-item navtab-x2 active pull-left");
            $("div[name='navtab1']").attr("class", "navtab-item navtab-x2 pull-left");

        });

        this.$('.appdown2', this._dom).on('click', __services.downloadService.download);
        this.$('.download', this._dom).on('click', __services.downloadService.download);

        var search = violet.url.search;
        this.$('.face-like').unbind("click");
        this.$('.face-like').on('click',
        function() {
            __services.httpService.request('/share/like', 'post', {
                "_id": search._id
            },
            function(err, metadata, data) {
                // this.$('.dialog-box').show()
                if (!err) {

                    var likeNum = data.sharedObject.numLike;
                    var unlikeNum = data.sharedObject.numDislike;
                    var likepercent = parseInt(likeNum / (likeNum + unlikeNum) * 100);
                     var unlikepercent = 100 - likepercent;

                    $("span[name='numlike']").html(likepercent + "%");
                    $("span[name='numunlike']").html(unlikepercent + "%");
                    this.$('.dialog-box').show()
                } else {
                    alert("投票失败");
                }
            });
        });

        this.$('.face-unlike').unbind("click");
        this.$('.face-unlike').on('click',function() {
            __services.httpService.request('/share/dislike', 'post', {
                "_id": search._id
            },
            function(err, metadata, data) {
                if (!err) {
                    var likeNum = data.sharedObject.numLike;
                    var unlikeNum = data.sharedObject.numDislike;
                    var likepercent = parseInt(likeNum / (likeNum + unlikeNum) * 100);
                    var unlikepercent = 100 - likepercent;

                    $("span[name='numlike']").html(likepercent + "%");
                    $("span[name='numunlike']").html(unlikepercent + "%");
                    this.$('.dialog-box').show()
                } else {
                    alert("投票失败");
                }
            });
        });


        this.$('.face-normal').unbind("click");
        this.$('.face-normal').on('click',function() {

            if(shareObj)
            {
                 __services.httpService.request('/share/query', 'get', {
                        '_ids' : [search._id]
                    }, function(err, metadata, data) {
                        var shareObj = data && data.sharedObjects && data.sharedObjects[0];
                        if (err || !shareObj) {
                            alert("查询投票失败");
                        } 
                        else {

                            var likeNum =  shareObj.numLike;
                            var unlikeNum = shareObj.numDislike;
                            
                            if(likeNum>0 || unlikeNum>0)
                            {
                                var likepercent = parseInt(likeNum / (likeNum + unlikeNum) * 100);
                                var unlikepercent = 100 - likepercent;
                                $("span[name='numlike']").html(likepercent + "%");
                                $("span[name='numunlike']").html(unlikepercent + "%");
                            }
                            else
                            {
                                $("span[name='numlike']").html("50%");
                                $("span[name='numunlike']").html("50%");
                            }
                            $('.dialog-box').show();
                        }
                    })
            }
        });
    }

    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    return P02ShareShow;
});