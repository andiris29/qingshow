//
//  QSPaymentService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/26/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#define SHARE_PAYMENT_SERVICE [QSPaymentService shareService]

@interface QSPaymentService : NSObject

+ (QSPaymentService*)shareService;

- (void)payWithAliPayTradeId:(NSString*)tradeId
                 productName:(NSString*)productName;
- (void)payWithWechatTradeId:(NSString*)tradeId
                    prepayId:(NSString*)prepayId
                 productName:(NSString*)productName;
@end
