//
//  QSNetworkEngine+FeedingAggregation.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/23.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine+FeedingAggregation.h"
#import "MKNetworkEngine+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "QSDateUtil.h"

#define PATH_FEEDING_AGGREGATION_FEATURED_TOP_OWNERS @"feedingAggregation/featuredTopOwners"
#define PATH_FEEDING_AGGREGATION_MATCH_HOT_TOP_OWNERS @"feedingAggregation/matchHotTopOwners"
#define PATH_FEEDING_AGGREGATION_MATCH_NEW @"feedingAggregation/matchNew"


@implementation QSNetworkEngine(FeedingAggregation)

- (MKNetworkOperation*)aggregationFeaturedTopOwners:(NSDate*)date onSucceed:(TopOwnerBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_FEEDING_AGGREGATION_FEATURED_TOP_OWNERS method:@"GET" paramers:@{@"date": [date description]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [self _handleTopOwner:completedOperation succeedBlock:succeedBlock];
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)aggregationMatchHotTopOwners:(NSDate*)date onSucceed:(TopOwnerBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_FEEDING_AGGREGATION_MATCH_HOT_TOP_OWNERS method:@"GET" paramers:@{@"date": [date description]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [self _handleTopOwner:completedOperation succeedBlock:succeedBlock];
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)aggregationMatchNew:(NSDate*)date onSucceed:(ArraySuccessBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_FEEDING_AGGREGATION_MATCH_NEW method:@"GET" paramers:@{@"date" : [date description]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* resJson = completedOperation.responseJSON;
            NSDictionary* data = [resJson dictValueForKeyPath:@"data"];
            NSMutableArray* retArray = [@[] mutableCopy];
            
            for (NSInteger i = 24; i >= 0; --i) {
                NSString* hourStr = @(i).stringValue;
                NSDictionary* dict = [data dictValueForKeyPath:hourStr];
                NSArray* topOwners = [dict arrayValueForKeyPath:@"topOwners"];
                if (topOwners && topOwners.count) {
                    [retArray addObject:@{
                                          @"hour" : @(i),
                                          @"date" : date,
                                          @"data" : dict
                                          }];
                }
            }
            succeedBlock(retArray, nil);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

#pragma mark - Private
- (void)_handleTopOwner:(MKNetworkOperation*)op
           succeedBlock:(TopOwnerBlock)block {
    if (block) {
        NSDictionary* resJson = op.responseJSON;
        NSDictionary* data = [resJson dictValueForKeyPath:@"data"];
        NSArray* owners = [data arrayValueForKeyPath:@"topOwners"];
        NSNumber* numOwn = [data numberValueForKeyPath:@"numOwners"];
        NSNumber* index = [data numberValueForKeyPath:@"indexOfCurrentUser"];
        
        block(owners, numOwn.intValue, index.intValue);
    }
}

@end
