//
//  QSNetworkEngine+TopicService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//
#import "QSNetworkEngine.h"
#import "QSNetworkEngine+Protect.h"
#import "NSArray+QSExtension.h"
#define URL_TOPIC_QUERY @"topic/query"

@implementation QSNetworkEngine(TopicService)

- (MKNetworkOperation*)queryTopicPage:(int)page
                            OnSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:URL_TOPIC_QUERY method:@"GET" paramers:@{@"pageNo": @(page), @"pageSize" : @10} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary* retDict = completedOperation.responseJSON;
            succeedBlock(([(NSArray*)retDict[@"data"][@"topics"] deepMutableCopy]), retDict[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}


@end
