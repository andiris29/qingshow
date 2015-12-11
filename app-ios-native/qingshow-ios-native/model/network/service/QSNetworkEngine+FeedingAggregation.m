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

#define PATH_FEEDING_AGGREGATION_LATEST @"feedingAggregation/latest"


@implementation QSNetworkEngine(FeedingAggregation)


- (MKNetworkOperation*)feedingAggregationOnSucceed:(ArraySuccessBlock)succeedBlock
                                           onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_FEEDING_AGGREGATION_LATEST
                                 method:@"GET"
                               paramers:@{}
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* resJson = completedOperation.responseJSON;
            NSDictionary* data = [resJson dictValueForKeyPath:@"data"];
            NSMutableArray* retArray = [@[] mutableCopy];
            NSDate* now = [NSDate date];
            NSInteger hour = [QSDateUtil getHourNumber:now];
            NSDate* day = [QSDateUtil clearTimeFromDate:now];

            for (NSInteger i = 0; i < 24; i++) {
                NSString* hourStr = @(hour - i).stringValue;
                NSDictionary* dict = [data dictValueForKeyPath:hourStr];
                NSArray* topOwners = [dict arrayValueForKeyPath:@"topOwners"];
                if (topOwners && topOwners.count) {
                    NSDate* targetDay = day;
                    NSInteger targetHour = hour - i;
                    if (targetHour < 0) {
                        targetDay = [day dateByAddingTimeInterval:- 24 * 60 * 60];
                        targetHour += 24;
                    }
                    
                    [retArray addObject:@{
                                          @"hour" : @(targetHour),
                                          @"date" : targetDay,
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

@end
