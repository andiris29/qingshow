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

#define PATH_FEEDING_AGGREGATION_FEATURED_TOP_OWNERS @"feedingAggregation/featuredTopOwners"
#define PATH_FEEDING_AGGREGATION_MATCH_HOT_TOP_OWNERS @"feedingAggregation/matchHotTopOwners"
#define PATH_FEEDING_AGGREGATION_MATCH_NEW @"feedingAggregation/matchNew"


@implementation QSNetworkEngine(FeedingAggregation)

- (MKNetworkOperation*)aggregationFeaturedTopOwners:(NSDate*)date onSucceed:(TopOwnerBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    NSString* formatStr = [self _paramStringFromDate:date];
    return [self startOperationWithPath:PATH_FEEDING_AGGREGATION_FEATURED_TOP_OWNERS method:@"GET" paramers:@{@"date": formatStr} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [self _handleTopOwner:completedOperation succeedBlock:succeedBlock];
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)aggregationMatchHotTopOwners:(NSDate*)date onSucceed:(TopOwnerBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    NSString* formatStr = [self _paramStringFromDate:date];
    return [self startOperationWithPath:PATH_FEEDING_AGGREGATION_MATCH_HOT_TOP_OWNERS method:@"GET" paramers:@{@"date": formatStr} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [self _handleTopOwner:completedOperation succeedBlock:succeedBlock];
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)aggregationMatchNew:(NSDate*)date onSucceed:(TopOwnerAndShowBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    NSString* formatStr = [self _paramStringFromDate:date];
    return [self startOperationWithPath:PATH_FEEDING_AGGREGATION_MATCH_NEW method:@"GET" paramers:@{@"date" : formatStr} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* resJson = completedOperation.responseJSON;
            NSDictionary* data = [resJson dictValueForKeyPath:@"data"];
            NSArray* owners = [data arrayValueForKeyPath:@"topOwners"];
            NSNumber* numOwn = [data numberValueForKeyPath:@"numOwners"];
            NSNumber* index = [data numberValueForKeyPath:@"indexOfCurrentUser"];
            NSArray* shows = [data arrayValueForKeyPath:@"topShows"];
#warning TODO
            /*
             feedingAggregation/matchNew
             * Response
             * `data.x.topOwners` array of db.peoples
             * `data.x.numOwners` int
             * `data.x.indexOfCurrentUser` int
             * `data.x.topShows` array of db.shows
             */
            succeedBlock(owners, numOwn.intValue, index.intValue, shows);
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
- (NSString*)_paramStringFromDate:(NSDate*)date {
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd"];
    [dateFormatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
    NSString* formatStr = [dateFormatter stringFromDate:date];
    return formatStr;
}
@end
