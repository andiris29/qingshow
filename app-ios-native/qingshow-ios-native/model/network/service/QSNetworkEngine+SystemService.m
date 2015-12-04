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
#define PATH_SYSTEM_LOG @"system/log"
#define PATH_SYSTEM_GET_CONFIG @"system/getConfig"

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
    
    return [self startOperationWithPath:PATH_SYSTEM_LOG method:@"POST" paramers:paramDict onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (successBlock) {
            successBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)systemGetConfigOnSucceed:(DicBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_SYSTEM_GET_CONFIG method:@"GET" paramers:@{} onSucceeded:^(MKNetworkOperation *completedOperation) {
        //data.config.event.image
        if (succeedBlock) {
            NSDictionary* retDict = completedOperation.responseJSON;
            NSDictionary* configDict = [retDict dictValueForKeyPath:@"data"];
            succeedBlock(configDict);
        }
        
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

@end
