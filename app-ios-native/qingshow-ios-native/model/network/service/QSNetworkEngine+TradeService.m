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
#import "QSEntityUtil.h"
#import "QSTradeUtil.h"


#define PATH_TRADE_QUERY @"trade/query"
#define PATH_TRADE_CREATE @"trade/create"
#define PATH_TRADE_QUERY_CREATED_BY @"trade/queryCreatedBy"
#define PATH_TRADE_REFRESH_PAYMENT_STATUS @"trade/refreshPaymentStatus"
#define PAHT_TRADE_STATUS_TO @"trade/statusTo"
#define PATH_TRADE_PREPAY @"trade/prepay"
#define PATH_TRADE_SHARE @"trade/share"
#define PATH_TRADE_QUERY_PHASES @"trade/queryByPhases"

@implementation QSNetworkEngine(TradeService)

#pragma mark - Create
- (MKNetworkOperation*)createOrderArray:(NSArray*)orderArray
                              onSucceed:(DicBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;
{
    if (!orderArray.count) {
#warning TODO
        errorBlock(nil);
    }
    NSDictionary* order = orderArray[0];
    return [self startOperationWithPath:PATH_TRADE_CREATE
                                 method:@"POST"
                               paramers:order
            
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
- (MKNetworkOperation*)queryTradeListPage:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSDictionary* userInfo = [QSUserManager shareUserManager].userInfo;
    NSString* userId = [QSEntityUtil getIdOrEmptyStr:userInfo];
    return [self queryTradeCreatedBy:userId page:page onSucceed:succeedBlock onError:errorBlock];
}

- (MKNetworkOperation*)queryTradeListPage:(int)page
                               inProgress:(NSString *)inProgress
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSDictionary* userInfo = [QSUserManager shareUserManager].userInfo;
    NSString* userId = [QSEntityUtil getIdOrEmptyStr:userInfo];
    return [self queryTradeCreatedBy:userId page:page inProgress:inProgress onSucceed:succeedBlock onError:errorBlock];
}

- (MKNetworkOperation*)queryPhase:(int)page
                                   phases:(NSString *)phases
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSDictionary *userInfo = [QSUserManager shareUserManager].userInfo;
    NSString *userId = [QSEntityUtil getIdOrEmptyStr:userInfo];
    return [self queryTradePhases:userId page:page phases:phases onSucceed:succeedBlock onError:errorBlock];
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

- (MKNetworkOperation*)queryTradeCreatedBy:(NSString*)peopleId
                                      page:(int)page
                                inProgress:(NSString *)inProgress
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_TRADE_QUERY_CREATED_BY
                                 method:@"GET"
                               paramers:@{@"_id" : peopleId,
                                          @"pageNo" : @(page),
                                          @"inProgress":inProgress,
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
- (MKNetworkOperation*)queryTradePhases:(NSString*)peopleId
                                      page:(int)page
                                phases:(NSString *)phases
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_TRADE_QUERY_CREATED_BY
                                 method:@"GET"
                               paramers:@{@"_id" : peopleId,
                                          @"pageNo" : @(page),
                                          @"inProgress":phases,
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
    return [self startOperationWithPath:PATH_TRADE_QUERY_PHASES method:@"POST" paramers:@{@"_id" : [QSEntityUtil getIdOrEmptyStr:tradeDict]} onSucceeded:^(MKNetworkOperation *completedOperation) {
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
                              info:(NSDictionary*)dict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;
{
    NSMutableDictionary* paramDict = nil;
    if (dict) {
        paramDict = [dict mutableCopy];
    } else {
        paramDict = [@{} mutableCopy];
    }
    
    paramDict[@"_id"] = [QSEntityUtil getIdOrEmptyStr:tradeDict];
    paramDict[@"status"] = @(status);
    
    return [self startOperationWithPath:PAHT_TRADE_STATUS_TO
                                 method:@"POST"
                               paramers:paramDict
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

- (MKNetworkOperation*)tradeShare:(NSDictionary*)tradeDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_TRADE_SHARE method:@"POST" paramers:@{@"_id" : [QSEntityUtil getIdOrEmptyStr:tradeDict]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
@end
