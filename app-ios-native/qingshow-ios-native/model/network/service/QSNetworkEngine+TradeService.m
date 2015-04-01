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
#import "QSUserManager.h"
#import "QSCommonUtil.h"


#define PATH_TRADE_CREATE @"trade/create"
#define PATH_TRADE_QUERY_CREATED_BY @"trade/queryCreatedBy"

@implementation QSNetworkEngine(TradeService)

- (MKNetworkOperation*)createTradeTotalFee:(double)totalFee
                                  quantity:(int)quantity
                                     price:(double)price
                                      item:(NSDictionary*)item
                                       sku:(NSNumber*)sku
                              receiverUuid:(NSString*)uuid
                                 onSucceed:(DicBlock)succeedBlock
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

- (MKNetworkOperation*)createTradeTotalFee:(double)totalFee
                                orderArray:(NSArray*)orderArray
                                 onSucceed:(DicBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_TRADE_CREATE
                                 method:@"POST"
                               paramers:@{ @"totalFee" : @(totalFee),
                                           @"orders" : orderArray}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    NSDictionary* retDict = completedOperation.responseJSON;
                    succeedBlock(retDict[@"data"][@"trade"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation*)queryOrderListPage:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSDictionary* userInfo = [QSUserManager shareUserManager].userInfo;
    NSString* userId = [QSCommonUtil getIdOrEmptyStr:userInfo];
    return [self queryTradeCreatedBy:userId page:page onSucceed:succeedBlock onError:errorBlock];
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
