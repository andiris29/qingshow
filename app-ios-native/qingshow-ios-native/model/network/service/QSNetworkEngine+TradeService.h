//
//  QSNetworkEngine+TradeService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"
typedef NS_ENUM(NSUInteger, PaymentType) {
    PaymentTypeAlipay,
    PaymentTypeWechat
};

@interface QSNetworkEngine(TradeService)

#pragma mark - Create
- (MKNetworkOperation*)createOrderArray:(NSArray*)orderArray
                              onSucceed:(DicBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;
#pragma mark - Query
- (MKNetworkOperation*)queryTradeDetail:(NSString*)tradeId
                              onSucceed:(DicBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)queryTradeListPage:(int)page
                               inProgress:(NSString *)inProgress
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)queryTradeListPage:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)refreshTradePaymentStatus:(NSDictionary*)tradeDict
                                       onSucceed:(DicBlock)succeedBlock
                                         onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)changeTrade:(NSDictionary*)tradeDict
                            status:(int)status
                              info:(NSDictionary*)dict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)prepayTrade:(NSDictionary*)tradeDict
                              type:(PaymentType)paymentType
                      receiverUuid:(NSString*)uuid
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;
@end
