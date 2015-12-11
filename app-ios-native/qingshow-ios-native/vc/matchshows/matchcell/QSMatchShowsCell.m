//
//  QSMatchShowsCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatchShowsCell.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSDateUtil.h"
#import "QSImageNameUtil.h"
#import "NSDictionary+QSExtension.h"
@implementation QSMatchShowsCell
{
    NSDictionary *_showDic;
    NSDictionary *_peopleDic;
}
- (void)layoutSubviews
{
    [super layoutSubviews];
    float ratioX = 1;
    float ratioY =1;
    float leftMargin = 6*ratioX;
    float topMargin = 54*ratioY;
    float weight = 138*ratioX;
    float height = 191.8*ratioY;
    
    self.matchShowImgview.frame =
    CGRectMake(
               leftMargin,
               topMargin,
               weight,
               height);
}

- (void)awakeFromNib {
    // Initialization code

    self.headerImgView.layer.masksToBounds = YES;
    self.headerImgView.layer.cornerRadius = 15.0;
    self.headerImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer *u01Tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(headerImgViewPressed:)];
    u01Tap.numberOfTapsRequired = 1;
    [self.headerImgView addGestureRecognizer:u01Tap];
    

    
    UITapGestureRecognizer *s03Tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(matchShowImgviewPressed:)];
    UITapGestureRecognizer *s03ImgTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(matchShowImgviewPressed:)];
    self.matchShowImgview.userInteractionEnabled = YES;
    self.bgImgView.userInteractionEnabled = YES;
    [self.matchShowImgview addGestureRecognizer:s03Tap];
    [self.bgImgView addGestureRecognizer:s03ImgTap];

    UITapGestureRecognizer* userTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(headerImgViewPressed:)];
    [self.headerImageTapView addGestureRecognizer:userTap];
    
}

- (void)bindWithDic:(NSDictionary *)dict withIndex:(int)index
{

    _showDic = dict;
    _peopleDic = [QSShowUtil getPeopleFromShow:dict];
    NSURL *headerIconUrl = [QSPeopleUtil getHeadIconUrl:_peopleDic type:QSImageNameType100];
    if (headerIconUrl) {
        [self.headerImgView setImageFromURL:headerIconUrl];
        
    }else{
        NSURL *defaultHeader = [QSImageNameUtil appendingDefaultImageUrl];
        [self.headerImgView setImageFromURL:defaultHeader];
    }
    
    self.rankImgView.image = [QSPeopleUtil rankImgView:_peopleDic];
    
    NSURL *url = [QSImageNameUtil appendImageNameUrl:[QSShowUtil getCoverUrl:dict] type:QSImageNameTypeS];
    [self.matchShowImgview setImageFromURL:url];
    [self.bgImgView setImageFromURL:[QSShowUtil getFormatterCoVerForegroundUrl:dict]];

    NSDate *createDate = [QSShowUtil getCreatedDate:dict];
    self.timeLabel.text = [QSDateUtil gettimeSinceDate:createDate];
    self.viewNumlabel.text = [QSShowUtil getNumberViewDesc:dict];
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:dict];
    self.userNameLabel.text = [QSPeopleUtil getNickname:peopleDict];
}

- (void)headerImgViewPressed:(id )sender
{
    if ([self.delegate respondsToSelector:@selector(headerImgViewPressed:)]) {
        [self.delegate headerImgViewPressed:_peopleDic];
    }
}
- (void)matchShowImgviewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(matchImgViewPressed:)]) {
        [self.delegate matchImgViewPressed:_showDic];
    }
}

- (void)cancelImageLoading {
    for (UIImageView* imgView in @[self.headerImgView, self.matchShowImgview, self.bgImgView]) {
        [imgView cancelImageLoadingOperation];
    }
}
@end
