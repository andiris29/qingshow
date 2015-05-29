//
//  QSNetworkEngine+TraceService.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine+TraceService.h"
#import "NSArray+QSExtension.h"
#import "QSNetworkEngine+Protect.h"
#import "QSCommonUtil.h"
#import "QSDateUtil.h"

#define PATH_LOG_TRACE @"log/trace"

@implementation QSNetworkEngine(TraceService)

- (MKNetworkOperation*)logTraceWithParametes:(NSDictionary *)parametes onSucceed:(EntitySuccessBlock)succeedBlock onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_LOG_TRACE
                                 method:@"POST"
                               paramers:parametes
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                if (succeedBlock) {
#warning TODO Trace response
//                    succeedBlock(retDict[@"data"],retDict[@"metadata"]);
                }
                            }
                                onError:
            ^(MKNetworkOperation *completedOperation, NSError *error) {
                if(errorBlock) {
                    errorBlock(error);
                }
            }
            ];

}
@end
