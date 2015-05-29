//
//  QSU02UserSettingImgCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02UserSettingImgCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSPeopleUtil.h"
@implementation QSU02UserSettingImgCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)imgCellBindWithDic:(NSDictionary *)peopleDic
{
    if (self.row == 0) {
        [self.headImgView setImageFromURL:[ QSPeopleUtil getHeadIconUrl:peopleDic]];
        self.titleLabel.text = @"个人头像";
    }
    else if(self.row == 1)
    {
        [self.headImgView setImageFromURL:[QSPeopleUtil getBackgroundUrl:peopleDic]];
        self.titleLabel.text = @"背景图片";
    }
    
}

@end
