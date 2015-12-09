//
//  QSNetworkEngine+TradeService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+TradeService.h"
#import "MKNetworkEngine+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"
#import "QSUserManager.h"
#import "QSEntityUtil.h"
#import "QSTradeUtil.h"
#import "QSPaymentService.h"

#define PATH_TRADE_QUERY @"trade/query"
#define PATH_TRADE_CREATE @"trade/create"
#define PATH_TRADE_PREPAY @"trade/prepay"
#define PATH_TRADE_RETURN @"trade/return"
#define PATH_TRADE_OWN @"trade/own"
#define PATH_TRADE_QUERY_HIGILIGHTED @"trade/queryHighlighted"
#define PATH_TRADE_RECEIVER @"trade/getReturnReceiver"
@implementation QSNetworkEngine(TradeService)

#pragma mark - Create
- (MKNetworkOperation*)createTradeItemRef:(NSString*)itemRef
                              promoterRef:(NSString*)promoterRef
                    selectedSkuProperties:(NSArray*)skuProperties
                                 quantity:(int)quantity
                                onSucceed:(DicBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{

    return [self startOperationWithPath:PATH_TRADE_CREATE
                                 method:@"POST"
                               paramers:@{
                                          @"promoterRef": promoterRef,
                                          @"itemRef" : itemRef,
                                          @"selectedSkuProperties" : skuProperties,
                                          @"quantity" : @(quantity)
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
- (MKNetworkOperation*)queryTradeDetail:(NSString*)tradeId
                              onSucceed:(DicBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_TRADE_QUERY method:@"GET" paramers:@{@"_ids" : tradeId} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* retDict = completedOperation.responseJSON;
            NSArray* trades = [retDict arrayValueForKeyPath:@"data.trades"];
            if (trades && trades.count) {
                NSDictionary* t = trades[0];
                succeedBlock(t);
            } else {
                succeedBlock(nil);
            }
        }

    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)queryTradeOwnPage:(int)page
                               onSucceed:(ArraySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPathNoVersion:PATH_TRADE_OWN
                                 method:@"GET"
                               paramers:@{
                                          @"pageNo" : @(page),
                                           @"pageSize" : @10
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    NSDictionary* retDict = completedOperation.responseJSON;
                    NSArray* trades = [retDict arrayValueForKeyPath:@"data.trades"];
                    succeedBlock([trades deepMutableCopy], [retDict dictValueForKeyPath:@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}



- (MKNetworkOperation*)prepayTrade:(NSDictionary*)tradeDict
                              type:(PaymentType)paymentType
                      receiverUuid:(NSString*)uuid
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    NSMutableDictionary* paramDict = [@{} mutableCopy];
    
    paramDict[@"_id"] = [QSEntityUtil getIdOrEmptyStr:tradeDict];
    paramDict[@"totalFee"] = [QSTradeUtil getTotalFee:tradeDict];
    paramDict[@"selectedPeopleReceiverUuid"] = uuid;
    NSDictionary* payTypeDict = @{};
    
    if (paymentType == PaymentTypeWechat) {
        payTypeDict = @{@"weixin" : @{@"prepayId" : [NSNull null]}};
    } else if (paymentType == PaymentTypeAlipay) {
        payTypeDict = @{@"alipay" : [NSNull null]};
    }
    paramDict[@"pay"] = payTypeDict;
    return [self startOperationWithPath:PATH_TRADE_PREPAY method:@"POST" paramers:paramDict onSucceeded:^(MKNetworkOperation *completedOperation) {
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

- (MKNetworkOperation*)tradeReturn:(NSDictionary*)tradeDict
                           company:(NSString*)companyName
                        trackingId:(NSString*)trackId
                           comment:(NSString*)comment
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    return [SHARE_NW_ENGINE startOperationWithPath:PATH_TRADE_RETURN
                                            method:@"POST"
                                          paramers:
            @{
              @"_id" : [QSEntityUtil getIdOrEmptyStr:tradeDict],
              @"returnLogistic" : @{
                              @"company" : companyName,
                              @"trackingId" : trackId
                              },
              @"note" : comment
              }
                                       onSucceeded:^(MKNetworkOperation *completedOperation) {
                                           if (succeedBlock) {
                                               NSDictionary* retDict = completedOperation.responseJSON;
                                               NSDictionary* data = [retDict dictValueForKeyPath:@"data.trade"];
                                               succeedBlock(data);
                                           }
    }
                                           onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
                                           }];
}

- (MKNetworkOperation*)tradeQueryHighted:(int)page
                               OnSecceed:(ArraySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPathNoVersion:PATH_TRADE_QUERY_HIGILIGHTED method:@"GET" paramers:@{@"pageNo":@(page),
                   @"pageSize":@10} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* retDict = completedOperation.responseJSON;
            NSArray* trades = retDict[@"data"][@"trades"];
            succeedBlock([trades deepMutableCopy],retDict[@"metadata"]);
        }

    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
- (MKNetworkOperation*)tradeReceiver:(NSString *)tradeId
                           onSucceed:(DicBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_TRADE_RECEIVER method:@"GET"
                               paramers:@{@"_id":tradeId}
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    NSDictionary* retDict = nil;
                                    id retData = completedOperation.responseJSON[@"data"][@"receiver"];
                                    if ([retData isKindOfClass:[NSDictionary class]])
                                    {
                                        retDict = retData;
                                    } else if ([retData isKindOfClass:[NSArray class]]) {
                                        NSArray* retArray = retData;
                                        if (retArray.count) {
                                            retDict = retArray[0];
                                        }
                                    }
                                    succeedBlock([retDict deepMutableCopy]);
                                }
                         
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
@end
