//
//  QSNetworkEngine+TradeService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+TradeService.h"
#import "QSNetworkEngine+Protect.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"


#define PATH_TRADE_CREATE @"trade/create"
#define PATH_TRADE_QUERY_CREATED_BY @"trade/queryCreatedBy"

@implementation QSNetworkEngine(TradeService)

- (MKNetworkOperation*)createTradeTotalFee:(long)totalFee
                                  quantity:(int)quantity
                                     price:(long)price
                                      item:(NSDictionary*)item
                                       sku:(NSString*)sku
                              receiverUuid:(NSString*)uuid
                                 onSucceed:(VoidBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self createTradeTotalFee:totalFee
                          orderArray:@[
                                       @{
                                           @"quantity" : @(quantity),
                                           @"price" : @(price),
                                           @"itemSnapshot" : item,
                                           @"selectedItemSkuId" : sku,
                                           @"selectedPeopleReceiverUuid" : uuid
                                           }
                                       ]
                           onSucceed:succeedBlock
                             onError:errorBlock];
}

- (MKNetworkOperation*)createTradeTotalFee:(long)totalFee
                                orderArray:(NSArray*)orderArray
                                 onSucceed:(VoidBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_TRADE_CREATE
                                 method:@"POST"
                               paramers:@{ @"totalFee" : @(totalFee),
                                           @"order" : orderArray}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    succeedBlock();
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation*)queryTradeCreatedBy:(NSString*)peopleId
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_TRADE_QUERY_CREATED_BY
                                 method:@"GET"
                               paramers:@{@"_id" : peopleId,
                                          @"pageNo" : @(page),
                                          @"pageSize" : @10 }
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    NSArray* trades = retDict[@"data"][@"trades"];
                    succeedBlock([trades deepMutableCopy], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

@end
