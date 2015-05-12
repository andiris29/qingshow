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
- (MKNetworkOperation*)createTradeTotalFee:(double)totalFee
                                  quantity:(int)quantity
                                     price:(double)price
                                      item:(NSDictionary*)item
                                       sku:(NSNumber*)sku
                              receiverUuid:(NSString*)uuid
                                      type:(PaymentType)paymentType
                                 onSucceed:(DicBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)createTradeTotalFee:(double)totalFee
                                orderArray:(NSArray*)orderArray
                                      type:(PaymentType)paymentType
                                 onSucceed:(DicBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;
#pragma mark - Query
- (MKNetworkOperation*)queryTradeCreatedBy:(NSString*)peopleId
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)queryOrderListPage:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)refreshTradePaymentStatus:(NSDictionary*)tradeDict
                                       onSucceed:(DicBlock)succeedBlock
                                         onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)changeTrade:(NSDictionary*)tradeDict
                            status:(int)status
                               onSucceed:(VoidBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;
@end
