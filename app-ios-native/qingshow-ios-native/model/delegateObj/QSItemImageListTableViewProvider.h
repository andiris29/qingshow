//
//  QSItemListTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSItemImageTableViewCell.h"
typedef NS_ENUM(NSInteger, QSItemImageListTableViewDelegateObjType) {
    QSItemImageListTableViewDelegateObjTypeNew,
    QSItemImageListTableViewDelegateObjTypeDiscount
};
@protocol  QSItemImageListTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

- (void)didClickShopBtnOfItem:(NSDictionary*)itemDict;

@end
@interface QSItemImageListTableViewProvider : QSTableViewBasicProvider<QSItemImageTableViewCellDelegate>

@property (assign, nonatomic) QSItemImageListTableViewDelegateObjType type;
@property (weak, nonatomic) NSObject<QSItemImageListTableViewProviderDelegate>* delegate;

@end
