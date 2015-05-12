//
//  QSNetworkEngine+ItemFeedingService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+Protect.h"
#import "QSNetworkEngine+ItemFeedingService.h"
#import "NSArray+QSExtension.h"
#import "QSCommonUtil.h"

#define ITEM_FEEDING_LIKE @"itemFeeding/like"



@implementation QSNetworkEngine(ItemFeedingService)
- (MKNetworkOperation*)getItemFeedingLikePage:(int)page
                                    onSucceed:(ArraySuccessBlock)successBlock
                                      onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:ITEM_FEEDING_LIKE
                                 method:@"GET"
                               paramers:@{@"pageNo":@(page), @"pageSize": @100}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (successBlock) {
                    NSArray* items = retDict[@"data"][@"items"];
                    successBlock([items deepMutableCopy], retDict[@"metadata"]);
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
