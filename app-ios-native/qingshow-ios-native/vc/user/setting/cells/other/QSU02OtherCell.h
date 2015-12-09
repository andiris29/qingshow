//
//  QSUserSettingOtherCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU02AbstractTableViewCell.h"

typedef NS_ENUM(NSInteger, U02SectionOtherRow) {
    U02SectionOtherRowPasswd = 0
};

@interface QSU02OtherCell : QSU02AbstractTableViewCell

+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType;
- (void)showDot;
- (void)hideDot;

@end
