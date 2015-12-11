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
- (MKNetworkOperation*)createTradeItemRef:(NSString*)itemRef
                              promoterRef:(NSString*)promoterRef
                    selectedSkuProperties:(NSArray*)skuProperties
                                 quantity:(int)quantity
                                onSucceed:(DicBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
#pragma mark - Query
- (MKNetworkOperation*)queryTradeDetail:(NSString*)tradeId
                              onSucceed:(DicBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)queryTradeOwnPage:(int)page
                               onSucceed:(ArraySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)prepayTrade:(NSDictionary*)tradeDict
                              type:(PaymentType)paymentType
                      receiverUuid:(NSString*)uuid
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)tradeReturn:(NSDictionary*)tradeDict
                           company:(NSString*)companyName
                        trackingId:(NSString*)trackId
                           comment:(NSString*)comment
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)tradeQueryHighted:(int)page
                                OnSecceed:(ArraySuccessBlock)succeedBlock
                                          onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)tradeReceiver:(NSString *)tradeId
                           onSucceed:(DicBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock;
@end
