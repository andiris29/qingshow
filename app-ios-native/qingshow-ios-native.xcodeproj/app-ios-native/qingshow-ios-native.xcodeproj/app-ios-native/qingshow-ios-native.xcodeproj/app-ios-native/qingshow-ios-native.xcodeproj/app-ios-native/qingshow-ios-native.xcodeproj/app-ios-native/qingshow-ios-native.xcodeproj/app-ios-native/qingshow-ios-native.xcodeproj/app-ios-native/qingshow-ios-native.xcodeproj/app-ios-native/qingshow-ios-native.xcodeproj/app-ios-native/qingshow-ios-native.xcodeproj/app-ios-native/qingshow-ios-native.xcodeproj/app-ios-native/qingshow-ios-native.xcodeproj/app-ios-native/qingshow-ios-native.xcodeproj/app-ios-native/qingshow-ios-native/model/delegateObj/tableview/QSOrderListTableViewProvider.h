//
//  QSOrderListTableViewProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/13/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSOrderListTableViewCell.h"

@protocol QSOrderListTableViewProviderDelegate <NSObject>

- (void)didClickRefundBtnOfOrder:(NSDictionary*)orderDict;
- (void)didClickSubmitBtnOfOrder:(NSDictionary*)orderDict;
- (void)didClickPayBtnOfOrder:(NSDictionary*)orderDict;
@end

@interface QSOrderListTableViewProvider : QSTableViewBasicProvider <QSOrderListTableViewCellDelegate>

@property (assign, nonatomic) CGFloat headerHeight;

@property (weak, nonatomic) NSObject<QSOrderListTableViewProviderDelegate>* delegate;

@end
