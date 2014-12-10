//
//  QSNetworkEngine+UserService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+UserService.h"
#import "QSUserManager.h"
#import "QSNetworkEngine+Protect.h"

//Path
#define PATH_USER_LOGIN @"user/login"
#define PATH_USER_LOGOUT @"user/logout"
#define PATH_USER_GET @"user/get"
#define PATH_USER_REGISTER @"user/register"
#define PATH_USER_UPDATE @"user/update"
#define PATH_USER_UPDATE_PORTRAIT @"user/updatePortrait"
#define PATH_USER_UPDATE_BACKGROUND @"user/updateBackground"


@implementation QSNetworkEngine(UserService)


#pragma mark - User
- (MKNetworkOperation*)loginWithName:(NSString*)userName
                            password:(NSString*)password
                           onSucceed:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    
    
    return [self startOperationWithPath:PATH_USER_LOGIN
                                 method:@"POST"
                               paramers:@{
                                          @"id" : userName,
                                          @"password" : password
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    NSDictionary *reDict = completedOperation.responseJSON;
                    [QSUserManager shareUserManager].userInfo = reDict[@"data"][@"people"];
                    [QSUserManager shareUserManager].fIsLogined = YES;
                    succeedBlock(reDict[@"data"][@"people"], reDict[@"metadata"]);
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
                    [QSUserManager shareUserManager].userInfo = nil;
                    [QSUserManager shareUserManager].fIsLogined = NO;
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

- (MKNetworkOperation *)updatePortrait:(NSData *)image
                             onSuccess:(EntitySuccessBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_USER_UPDATE_PORTRAIT
                                 method:@"POST"
                               paramers:@{}
                                fileKey:@"portrait"
                                  image:image
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    NSDictionary* retDict = completedOperation.responseJSON;
                    QSUserManager* manager = [QSUserManager shareUserManager];
                    manager.userInfo = retDict[@"data"][@"people"];
                    succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation *)updateBackground:(NSData *)image
                               onSuccess:(EntitySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock {
    
    
    return [self startOperationWithPath:PATH_USER_UPDATE_BACKGROUND
                                 method:@"POST"
                               paramers:@{}
                                fileKey:@"background"
                                  image:image
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    NSDictionary* retDict = completedOperation.responseJSON;
                    QSUserManager* manager = [QSUserManager shareUserManager];
                    manager.userInfo = retDict[@"data"][@"people"];
                    succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation *)getLoginUserOnSucced:(EntitySuccessBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    
    QSUserManager* manager = [QSUserManager shareUserManager];
    return [self startOperationWithPath:PATH_USER_GET
                                 method:@"GET"
                               paramers:nil
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary* retDict = completeOperation.responseJSON;
                manager.fIsLogined = YES;
                manager.userInfo = retDict[@"data"][@"people"];
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
                }
            }
                                onError:
            ^(MKNetworkOperation *completedOperation, NSError *error) {
                manager.fIsLogined = NO;
                manager.userInfo = nil;
                if (errorBlock) {
                    errorBlock(error);
                }
            }
            ];
}

- (MKNetworkOperation *)registerById:(NSString *) pid
                            Password:(NSString *)passwd
                           onSuccess:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock {
    
    return [self startOperationWithPath:PATH_USER_REGISTER
                                 method:@"POST"
                               paramers:@{@"id" : pid, @"password": passwd}
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                [QSUserManager shareUserManager].fIsLogined = YES;
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
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

- (MKNetworkOperation *)updatePeople:(NSDictionary *)people
                           onSuccess:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock {
    
    return [self startOperationWithPath:PATH_USER_UPDATE
                                 method:@"POST"
                               paramers:people
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
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
