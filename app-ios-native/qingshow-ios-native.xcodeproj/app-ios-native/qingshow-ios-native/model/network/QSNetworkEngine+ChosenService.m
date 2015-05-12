//
//  QSNetworkEngine+ChosenService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+ChosenService.h"
#import "QSNetworkEngine+Protect.h"
#import "NSArray+QSExtension.h"

#define URL_CHOSEN_FEED @"chosen/feed"

@implementation QSNetworkEngine(ChosenService)

- (MKNetworkOperation*)chosenFeedByType:(ChosenType)type
                    page:(int)page
               onSucceed:(ArraySuccessBlock)succeedBlock
                 onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:URL_CHOSEN_FEED
                                 method:@"GET"
                               paramers:@{
                                          @"type" : @(type),
                                          @"pageNo" : @(page),
                                          @"pageSize" : @20
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation)
    {
        if (succeedBlock) {
            NSDictionary* retDict = completedOperation.responseJSON;
            NSArray* chosens = retDict[@"data"][@"chosens"];
            succeedBlock([chosens deepMutableCopy], retDict[@"metadata"]);
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
