//
//  QST01ShowTradeProvider.h
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"

@protocol QST01ShowTradeProviderDelegate <NSObject>

- (void)didTapTradeCell:(NSString *)ItemId;

@end

@interface QST01ShowTradeProvider : QSTableViewBasicProvider<QSAbstractScrollProviderDelegate>

@property (weak, nonatomic)NSObject<QST01ShowTradeProviderDelegate>* delegate;

@end
