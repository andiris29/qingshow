//
//  QSU02UserSettingImgCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU02AbstractTableViewCell.h"

typedef NS_ENUM(NSInteger, U02SectionImageRow) {
    U02SectionImageRowHead = 0,
    U02SectionImageRowBackground,
};

@interface QSU02ImgCell : QSU02AbstractTableViewCell
+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType;

@property (weak, nonatomic) IBOutlet UIImageView *headImgView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

- (void)bindWithUser:(NSDictionary *)userDict;
@end
