//
//  QSItemListTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTableViewBasicDelegateObj.h"
#import "QSItemImageTableViewCell.h"
typedef NS_ENUM(NSInteger, QSItemImageListTableViewDelegateObjType) {
    QSItemImageListTableViewDelegateObjTypeNew,
    QSItemImageListTableViewDelegateObjTypeDiscount
};
@protocol  QSItemImageListTableViewDelegateObjDelegate <QSAbstractScrollDelegateObjDelegate>

- (void)didClickShopBtnOfItem:(NSDictionary*)itemDict;

@end
@interface QSItemImageListTableViewDelegateObj : QSTableViewBasicDelegateObj<QSItemImageTableViewCellDelegate>

@property (assign, nonatomic) QSItemImageListTableViewDelegateObjType type;
@property (weak, nonatomic) NSObject<QSItemImageListTableViewDelegateObjDelegate>* delegate;

@end
