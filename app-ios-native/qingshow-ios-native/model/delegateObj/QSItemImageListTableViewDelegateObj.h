//
//  QSItemListTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTableViewBasicDelegateObj.h"
typedef NS_ENUM(NSInteger, QSItemImageListTableViewDelegateObjType) {
    QSItemImageListTableViewDelegateObjTypeNew,
    QSItemImageListTableViewDelegateObjTypeDiscount
};

@interface QSItemImageListTableViewDelegateObj : QSTableViewBasicDelegateObj

@property (assign, nonatomic) QSItemImageListTableViewDelegateObjType type;

@end
