//
//  QST01ShowTradeProvider.h
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QST01ShowTradeCell.h"
@protocol QST01ShowTradeProviderDelegate <NSObject>

- (void)didTapTradeCell:(NSString *)ItemId;
- (void)didTapHeaderInT01Cell:(NSDictionary *)peopleDic;

@end

@interface QST01ShowTradeProvider : QSTableViewBasicProvider<QSAbstractScrollProviderDelegate,QST01ShowTradeCellDelegate>

@property (weak, nonatomic)NSObject<QST01ShowTradeProviderDelegate>* delegate;

@end
