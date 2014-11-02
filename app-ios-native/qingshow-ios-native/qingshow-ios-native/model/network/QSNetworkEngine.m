//
//  QSNetworkEngine.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine.h"
#import "ServerPath.h"

#define PATH_USER_LOGIN @"user/login"
#define PATH_USER_LOGOUT @"user/logout"


@implementation QSNetworkEngine

#pragma mark - Static Method
+ (QSNetworkEngine*)shareNetworkEngine
{

    static QSNetworkEngine* s_networkEngine = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_networkEngine = [[QSNetworkEngine alloc] initWithHostName:HOST_NAME];
    });
    return s_networkEngine;
    
    return nil;
}

#pragma mark - Basic
- (MKNetworkOperation*)startOperationWithPath:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    op = [self operationWithPath:path params:paramDict httpMethod:method ];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}


#pragma mark - API
- (MKNetworkOperation*)loginWithName:(NSString*)userName
                            password:(NSString*)password
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    
    
    return [self startOperationWithPath:PATH_USER_LOGIN
                                 method:@"POST"
                               paramers:@{
                                          @"mail" : userName,
                                          @"encryptedPassword" : password
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation)
    {
        if (succeedBlock) {
            succeedBlock();
        }
    }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
    {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
- (MKNetworkOperation*)logoutOnSucceed:(VoidBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_USER_LOGOUT
                                 method:@"POST"
                               paramers:@{}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    succeedBlock();
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
