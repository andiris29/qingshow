//
//  QSNetworkEngine+SystemService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine+SystemService.h"
#import "MKNetworkEngine+QSExtension.h"
#import "NSDictionary+QSExtension.h"

@implementation QSNetworkEngine(SystemService)
- (MKNetworkOperation*)systemLogLevel:(NSString*)level
                              message:(NSString*)message
                                stack:(NSString*)stack
                                extra:(NSString*)extra
                            onSuccess:(VoidBlock)successBlock
                              onError:(ErrorBlock)errorBlock {
    NSMutableDictionary* paramDict = [@{} mutableCopy];
    paramDict[@"client"] = @"ios";
    if (level) {
        paramDict[@"level"] = level;
    }
    if (message) {
        paramDict[@"message"] = message;
    }
    if (stack) {
        paramDict[@"stack"] = stack;
    }
    if (extra) {
        paramDict[@"extra"] = extra;
    }
    
    return [self startOperationWithPath:@"system/log" method:@"POST" paramers:paramDict onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (successBlock) {
            successBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
@end
