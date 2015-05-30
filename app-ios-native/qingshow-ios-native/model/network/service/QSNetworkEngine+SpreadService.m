//
//  QSNetworkEngine+TraceService.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine+SpreadService.h"
#import "NSArray+QSExtension.h"
#import "QSNetworkEngine+Protect.h"
#import "QSCommonUtil.h"
#import "QSDateUtil.h"
#import "QSUserManager.h"



#define PATH_LOG_TRACE @"spread/firstLaunch"

@implementation QSNetworkEngine(SpreadService)

- (MKNetworkOperation*)logTraceWithParametes:(NSDictionary *)parametes
                                   onSucceed:(BoolBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock
{   
    return [self startOperationWithPath:PATH_LOG_TRACE
                                 method:@"POST"
                               paramers:parametes
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                NSString* n = [retDict valueForKeyPath:@"data.trace.behaviorInfo.firstLaunch.channel"];
                BOOL f = NO;
                if (![QSCommonUtil checkIsNil:n] && n && n.length) {
                    f = YES;
                    NSDate* d = [NSDate dateWithTimeIntervalSinceNow:60 * 30];
                    [QSUserManager shareUserManager].globalFirstLaunchShowDueDate = d;
                    [QSUserManager shareUserManager].globalFirstLaunchTitle = n;
                }
                [[NSNotificationCenter defaultCenter] postNotificationName:kGlobalFirstUpdateNotification object:nil];
                if (succeedBlock) {
                    succeedBlock(f);
                }
                
            }
            onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
    
}
@end
