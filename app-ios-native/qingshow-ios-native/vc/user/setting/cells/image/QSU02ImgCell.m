//
//  QSU02UserSettingImgCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02ImgCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSPeopleUtil.h"
#import "UINib+QSExtension.h"
#import <QuartzCore/QuartzCore.h>
#import "QSImageNameUtil.h"

NSString* imgRowTypeToTitle(U02SectionImageRow type) {
    return @[@"个人头像", @"背景图片"][type];
}
NSURL* imgRowTypeToImgUrl(U02SectionImageRow type, NSDictionary* peopleDict) {
    switch (type) {
        case U02SectionImageRowHead:
            return [QSImageNameUtil appendImageNameUrl:[QSPeopleUtil getHeadIconUrl:peopleDict] type:QSImageNameType100];
        case U02SectionImageRowBackground:
            return [QSPeopleUtil getBackgroundUrl:peopleDict];
        default:
            return nil;
    }
}

@implementation QSU02ImgCell

+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType
{
    return [UINib generateViewWithNibName:@"QSU02ImgCell"];
}

- (void)awakeFromNib {
    // Initialization code
    self.headImgView.layer.cornerRadius = self.headImgView.bounds.size.width / 2;
    self.headImgView.layer.masksToBounds = YES;
    self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithUser:(NSDictionary *)peopleDic
{
    
    self.titleLabel.text = imgRowTypeToTitle(self.rowType);
    [self.headImgView setImageFromURL:imgRowTypeToImgUrl(self.rowType, peopleDic) placeHolderImage:[UIImage imageNamed:@"user_bg_default.jpg"]];
}
- (BOOL)cellDidClicked {
    [self.delegate prompToChangeImage:self.rowType];
    return YES;
}
@end
