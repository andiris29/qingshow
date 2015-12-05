//
//  QSNetworkEngine+BonusService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/1.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine+BonusService.h"
#import "MKNetworkEngine+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"

#define PATH_BONUS_QUERY @"bonus/query"
#define PATH_BONUS_OWN @"bonus/own"

@implementation QSNetworkEngine(BonusService)

- (MKNetworkOperation*)queryBonusWithIds:(NSArray*)bonusIds
                               onSucceed:(ArraySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock {
    NSMutableString* str = [@"" mutableCopy];
    for (int i = 0; i < bonusIds.count; i++) {
        NSString* bonusId = bonusIds[i];
        if (i != 0) {
            [str appendString:@","];
        }
        [str appendString:bonusId];
    }
    
    return [self startOperationWithPath:PATH_BONUS_QUERY
                                 method:@"GET"
                               paramers:@{
                                          @"_ids" : str}
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* retJson = completedOperation.responseJSON;
            NSArray* bonus = [retJson arrayValueForKeyPath:@"data.bonuses"];
            succeedBlock([bonus deepMutableCopy], nil);
        }
    }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
- (MKNetworkOperation*)queryOwnedBonusPage:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_BONUS_OWN
                                 method:@"GET"
                               paramers:@{@"pageNo" : @(page)}
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    NSDictionary* retJson = completedOperation.responseJSON;
                                    NSArray* bonus = [retJson arrayValueForKeyPath:@"data.bonuses"];
                                    succeedBlock([bonus deepMutableCopy], nil);
                                }
                            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
                                }];
}
@end
