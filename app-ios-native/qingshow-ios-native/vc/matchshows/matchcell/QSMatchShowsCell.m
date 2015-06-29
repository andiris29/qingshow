//
//  QSMatchShowsCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatchShowsCell.h"
#import "QSShowUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSMatchShowsCell

- (void)awakeFromNib {
    // Initialization code
    self.headerImgView.layer.masksToBounds = YES;
    self.headerImgView.layer.cornerRadius = 16.0;
    self.headerImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(headerImgViewPressed:)];
    [self.headerImgView addGestureRecognizer:tap];
}

- (void)bindWithDic:(NSDictionary *)dict
{
    if ([QSShowUtil getCoverUrl:dict]) {
        [self.matchShowImgview setImageFromURL:[QSShowUtil getCoverUrl:dict]];
    }
    if ([QSShowUtil getCoverForegroundUrl:dict]) {
        [self.headerImgView setImageFromURL:[QSShowUtil getCoverForegroundUrl:dict]];
    }
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

- (void)headerImageViewPressed:(NSString *)userid
{
    if (1) {
        
    }
}


@end
