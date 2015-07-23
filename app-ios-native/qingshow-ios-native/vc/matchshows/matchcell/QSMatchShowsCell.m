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
//     self.matchShowImgview.translatesAutoresizingMaskIntoConstraints = YES;
//    self.matchShowImgview.layer.masksToBounds = NO;
    self.headerImgView.layer.masksToBounds = YES;
    self.headerImgView.layer.cornerRadius = 16.0;
    self.headerImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer *u01Tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(headerImgViewPressed:)];
    u01Tap.numberOfTapsRequired = 1;
    [self.headerImageTapView addGestureRecognizer:u01Tap];
    
    
    UITapGestureRecognizer *s03Tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(matchShowImgviewPressed:)];
    UITapGestureRecognizer *s03ImgTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(matchShowImgviewPressed:)];
    self.matchShowImgview.userInteractionEnabled = YES;
    self.bgImgView.userInteractionEnabled = YES;
    [self.matchShowImgview addGestureRecognizer:s03Tap];
    [self.bgImgView addGestureRecognizer:s03ImgTap];
    
}

- (void)bindWithDic:(NSDictionary *)dict withIndex:(int)index
{

    _showDic = dict;
    _peopleDic = [QSShowUtil getPeopleFromShow:dict];
    [self.matchShowImgview setImageFromURL:[QSShowUtil getCoverUrl:dict]];
    int headNum = arc4random() % 6 + 1;
    NSString *headStr = [NSString stringWithFormat:@"http://trial01.focosee.com/img/user/portrait/%d@2x.png",headNum];
    if ([QSPeopleUtil getHeadIconUrl:_peopleDic type:QSImageNameType100]) {
        [self.headerImgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:_peopleDic type:QSImageNameType100]];
    }
    else
    {
        [self.headerImgView setImageFromURL:[NSURL URLWithString:headStr]];
    }
    
    [self.bgImgView setImageFromURL:[QSShowUtil getCoverForegroundUrl:dict]];
    NSString *createDate = dict[@"create"];
    //NSLog(@"%@",createDate);
    self.timeLabel.text = [QSDateUtil gettimeSinceDate:createDate];
    self.likeNumlabel.text = [QSShowUtil getNumberLikeDescription:dict];
    self.userNameLabel.text = [QSShowUtil getNameStr:dict];
    NSString *groupStr = [QSShowUtil getRecommendGroup:dict];
    if (groupStr) {
        if ([groupStr isEqualToString:@"1"]) {
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_thin"];
        }
        else if([groupStr isEqualToString:@"2"]){
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_nomal"];
        }
        else if ([groupStr isEqualToString:@"3"])
        {
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_heavy"];
        }
        else
        {
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_fat"];
        }
    }
    
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


@end
