//
//  QSUserSettingManagerCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU02AbstractTableViewCell.h"

typedef NS_ENUM(NSInteger, U02SectionManagerRow) {
    U02SectionManagerRowAddress = 0
};

@interface QSU02ManagerCell : QSU02AbstractTableViewCell
+ (QSU02AbstractTableViewCell*)generateCellWithRowType:(NSInteger)rowType;
@end
