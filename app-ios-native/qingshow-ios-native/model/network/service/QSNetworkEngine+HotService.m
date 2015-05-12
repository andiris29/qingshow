//
//  QSNetworkEngine+HotService.m
//  qingshow-ios-native
//
//  Created by Han Hugh on 15/5/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine+HotService.h"
#import "QSNetworkEngine+Protect.h"
#import "NSArray+QSExtension.h"

#define URL_HOT_FEEDING @"feeding/hot"

@implementation QSNetworkEngine (HotService)

- (MKNetworkOperation *)hotFeedingByOnSucceed:(ArraySuccessBlock)succeedBlock onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:URL_HOT_FEEDING method:nil paramers:nil onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary *topShows = completedOperation.responseJSON;
            NSArray *topShowsArray = topShows[@"data"][@"shows"];
            succeedBlock([topShowsArray deepMutableCopy], topShows[@"metadata"]);
        }
        
        
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

@end
