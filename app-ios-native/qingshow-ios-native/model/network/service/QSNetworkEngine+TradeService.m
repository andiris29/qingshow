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
#define PATH_TRADE_REFRESH_PAYMENT_STATUS @"trade/refreshPaymentStatus"
#define PAHT_TRADE_STATUS_TO @"trade/statusTo"

@implementation QSNetworkEngine(TradeService)

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
                                type:paymentType
                           onSucceed:succeedBlock
                             onError:errorBlock];
}

- (MKNetworkOperation*)createTradeTotalFee:(double)totalFee
                                orderArray:(NSArray*)orderArray
                                      type:(PaymentType)paymentType
                                 onSucceed:(DicBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;
{
    
    NSDictionary* payTypeDict = @{};
    if (paymentType == PaymentTypeWechat) {
        payTypeDict = @{@"weixin" : @{@"prepayId" : [NSNull null]}};
    } else if (paymentType == PaymentTypeAlipay) {
        payTypeDict = @{@"alipay" : [NSNull null]};
    }
    
    return [self startOperationWithPath:PATH_TRADE_CREATE
                                 method:@"POST"
                               paramers:@{ @"totalFee" : @(totalFee),
                                           @"orders" : orderArray,
                                           @"pay": payTypeDict
                                           }
            
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

#pragma mark - Query
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
                if (succeedBlock) {
                    NSDictionary* retDict = completedOperation.responseJSON;
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

- (MKNetworkOperation*)refreshTradePaymentStatus:(NSDictionary*)tradeDict
                                       onSucceed:(DicBlock)succeedBlock
                                         onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_TRADE_REFRESH_PAYMENT_STATUS method:@"POST" paramers:@{@"_id" : [QSCommonUtil getIdOrEmptyStr:tradeDict]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* retDict = completedOperation.responseJSON;
            succeedBlock(retDict[@"data"][@"trade"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)changeTrade:(NSDictionary*)tradeDict
                            status:(int)status
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock
{
    if (status == 3 || status == 14) {
        return [self startOperationWithPath:PAHT_TRADE_STATUS_TO
                                     method:@"POST"
                                   paramers:@{
                                              @"_id" : [QSCommonUtil getIdOrEmptyStr:tradeDict],
                                              @"status" : @(status),
                                              @"comments":[QSCommonUtil getCommentsStr:tradeDict]}
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
    return [self startOperationWithPath:PAHT_TRADE_STATUS_TO
                                 method:@"POST"
                               paramers:@{
                                          @"_id" : [QSCommonUtil getIdOrEmptyStr:tradeDict],
                                          @"status" : @(status)}
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
@end
