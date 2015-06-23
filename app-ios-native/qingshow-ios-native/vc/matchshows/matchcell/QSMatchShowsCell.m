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
    self.userNameLabel.text = [QSShowUtil getRecommentDesc:dict];
    NSString *groupStr = [QSShowUtil getRecommendGroup:dict];
    if (groupStr) {
        if ([groupStr isEqualToString:@"A1"]) {
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_thin"];
        }
        else if([groupStr isEqualToString:@"A2"]){
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_nomal"];
        }
        else if ([groupStr isEqualToString:@"A3"])
        {
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_heavy"];
        }
        else
        {
            self.bodyTypeImgView.image = [UIImage imageNamed:@"body_fat"];
        }
    }
    
}



@end
