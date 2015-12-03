//
//  QSNetworkEngine+UserService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+UserService.h"
#import "QSUserManager.h"
#import "MKNetworkEngine+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "QSReceiverUtil.h"

//Path
#define PATH_USER_LOGIN @"user/login"
#define PATH_USER_LOGIN_WECHAT @"user/loginViaWeixin"
#define PATH_USER_LOGIN_AS_GUEST @"user/loginAsGuest"

#define PATH_USER_LOGOUT @"user/logout"
#define PATH_USER_GET @"user/get"
#define PATH_USER_REGISTER @"user/register"
#define PATH_USER_UPDATE @"user/update"
#define PATH_USER_UPDATE_PORTRAIT @"user/updatePortrait"
#define PATH_USER_UPDATE_BACKGROUND @"user/updateBackground"
#define PATH_USER_SAVE_RECEIVER @"user/saveReceiver"
#define PATH_USER_REMOVE_RECEIVER @"user/removeReceiver"

#define PATH_MOBILE_REQUEST_CODE @"user/requestVerificationCode"
#define PATH_MOBILE_RESET_PASSWORD @"user/resetPassword"
#define PATH_MOBILE_FORGET_PASSWORD @"user/forgotPassword"
#define PATH_USER_READ_NOTIFICATION @"user/readNotification"

//Bind
#define PATH_BIND_JPUSH @"user/bindJPush"
#define PATH_BIND_MOBILE @"user/bindMobile"
#define PATH_BIND_WECHAT @"user/bindWeixin"

@implementation QSNetworkEngine(UserService)


#pragma mark - User
- (MKNetworkOperation*)loginWithName:(NSString*)userName
                            password:(NSString*)password
                           onSucceed:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{
                                        @"id" : userName,
                                        @"password" : password
                                        } mutableCopy];
    return [self startOperationWithPath:PATH_USER_LOGIN
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                [self _handleLoginSucceedOperation:completedOperation succeedBlock:succeedBlock];
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation*)loginAsGuestOnSucceed:(EntitySuccessBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock {
    NSMutableDictionary* paramDict = [@{} mutableCopy];
    return [self startOperationWithPath:PATH_USER_LOGIN_AS_GUEST
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                [self _handleLoginSucceedOperation:completedOperation succeedBlock:succeedBlock];
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}


- (MKNetworkOperation*)loginViaWechatCode:(NSString*)code
                                      onSucceed:(EntitySuccessBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock
{
    
    NSMutableDictionary* paramDict = [@{
                                        @"code" : code
                                        } mutableCopy];
    return [self startOperationWithPath:PATH_USER_LOGIN_WECHAT
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                [self _handleLoginSucceedOperation:completedOperation succeedBlock:succeedBlock];
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
    NSMutableDictionary* paramDict = [@{} mutableCopy];
    [QSUserManager shareUserManager].userInfo = nil;
    [QSUserManager shareUserManager].fIsLogined = NO;
    [[NSNotificationCenter defaultCenter] postNotificationName:kUserInfoUpdateNotification object:nil userInfo:nil];
    
    return [self startOperationWithPath:PATH_USER_LOGOUT
                                 method:@"POST"
                               paramers:paramDict
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

- (MKNetworkOperation *)updatePortrait:(NSData *)image
                             onSuccess:(EntitySuccessBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_USER_UPDATE_PORTRAIT
                                 method:@"POST"
                               paramers:@{}
                                fileKey:@"portrait"
                               fileName:@"portrait.jpeg"
                                  image:image
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    NSDictionary* retDict = completedOperation.responseJSON;
                    QSUserManager* manager = [QSUserManager shareUserManager];
                    manager.userInfo = [retDict dictValueForKeyPath:@"data.people"];
                    succeedBlock(manager.userInfo, [retDict dictValueForKeyPath:@"metadata"]);
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
                               fileName:@"background.jpeg"
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
                retDict = [retDict deepMutableCopy];
                manager.fIsLogined = YES;
                manager.userInfo = retDict[@"data"][@"people"];
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
                }
                [[NSNotificationCenter defaultCenter] postNotificationName:kUserInfoUpdateNotification object:nil userInfo:manager.userInfo];
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

- (MKNetworkOperation *)registerByMobile:(NSString *)mobile
                                  password:(NSString *)passwd
                              verifyCode:(NSString *)code
                             onSucceessd:(EntitySuccessBlock)successdBlock onErrer:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_USER_REGISTER
                                 method:@"POST"
                               paramers:@{@"mobile" : mobile, @"password": passwd, @"verificationCode":code}
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                [QSUserManager shareUserManager].fIsLogined = YES;
                
                NSDictionary* userDict = [retDict dictValueForKeyPath:@"data.people"];
                [QSUserManager shareUserManager].userInfo = userDict;
                [self userBindCurrentJpushIdOnSucceed:nil onError:nil];
                if (successdBlock) {
                    successdBlock(userDict, [retDict dictValueForKeyPath:@"metadata"]);
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
                [QSUserManager shareUserManager].userInfo = retDict[@"data"][@"people"];
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

- (MKNetworkOperation*)setDefaultReceiver:(NSDictionary*)receiverDict
                                onSuccess:(VoidBlock)successBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [receiverDict mutableCopy];
    [QSReceiverUtil setReceiver:paramDict isDefault:YES];
    return [self startOperationWithPath:PATH_USER_SAVE_RECEIVER
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (successBlock) {
                    successBlock();
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation *)saveReceiver:(NSString*)uuid
                                name:(NSString*)name
                               phone:(NSString*)phone
                            province:(NSString*)province
                             address:(NSString*)address
                           isDefault:(BOOL)isDefault
                           onSuccess:(void (^)(NSDictionary *people, NSString* uuid, NSDictionary *metadata))succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{
                                        @"name" : name,
                                        @"phone" : phone,
                                        @"province" : province,
                                        @"address" : address,
                                        @"isDefault" : @(isDefault)
                                        } mutableCopy];
    if (uuid) {
        paramDict[@"uuid"] = uuid;
    }
    
    return [self startOperationWithPath:PATH_USER_SAVE_RECEIVER
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"people"], retDict[@"data"][@"receiverUuid"], retDict[@"metadata"]);
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

- (MKNetworkOperation*)removeReceiver:(NSDictionary*)receiver
                            onSuccess:(VoidBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_USER_REMOVE_RECEIVER
                                 method:@"POST"
                               paramers:@{
                                   @"uuid" : receiver[@"uuid"]
                                   }
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    succeedBlock();
                                }
                            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
                                }
            ];
}

- (MKNetworkOperation*)getVerifyCodeForMobile:(NSString*)mobileNum
                                    onSucceed:(VoidBlock)succeedBlock
                                      onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_MOBILE_REQUEST_CODE method:@"POST" paramers:@{@"mobile":mobileNum} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation *)forgetPasswordPhone:(NSString *)mobileNum
                                 verifyCode:(NSString *)code
                                  onSucceed:(VoidBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_MOBILE_FORGET_PASSWORD
                                 method:@"POST"
                               paramers:@{
                                          @"mobile":mobileNum,
                                          @"verificationCode":code
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    succeedBlock();
                                }
                            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
                                }];
}

- (MKNetworkOperation *)resetPassword:(NSString*)newPassword
                            onSucceed:(VoidBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MOBILE_RESET_PASSWORD
                                 method:@"POST"
                               paramers:@{
                                          @"password" : newPassword
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    succeedBlock();
                                }
                            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
                                }];
}

- (MKNetworkOperation*)userReadNotification:(NSDictionary*)noti
                                  onSucceed:(VoidBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPathNoVersion:PATH_USER_READ_NOTIFICATION method:@"POST" paramers:noti onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}


#pragma mark - Bind
- (MKNetworkOperation*)userBindCurrentJpushIdOnSucceed:(VoidBlock)succeedBlock
                                               onError:(ErrorBlock)errorBlock {
    return [self userBindJpushId:[QSUserManager shareUserManager].JPushRegistrationID
                       onSucceed:succeedBlock
                         onError:errorBlock];
}

- (MKNetworkOperation*)userBindJpushId:(NSString*)jpushId
                             onSucceed:(VoidBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock {
    if (!jpushId) {
        return nil;
    }
    
    NSMutableDictionary* paramDict = [@{} mutableCopy];
    paramDict[@"registrationId"] = jpushId;
    return [self startOperationWithPath:PATH_BIND_JPUSH
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    succeedBlock();
                                }
                            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
                                }];
}

- (MKNetworkOperation*)userBindMobile:(NSString*)mobileNumber
                           verifyCode:(NSString*)code
                            onSucceed:(EntitySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_BIND_MOBILE
                                 method:@"POST"
                               paramers:@{
                                          @"mobile" : mobileNumber,
                                          @"verificationCode" : code
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                [self _handleLoginSucceedOperation:completedOperation succeedBlock:succeedBlock];
                            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
                                }];
}

- (MKNetworkOperation*)userBindWechat:(NSString*)code
                            onSucceed:(EntitySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_BIND_WECHAT method:@"POST" paramers:@{@"code" : code } onSucceeded:^(MKNetworkOperation *completedOperation) {
        [self _handleLoginSucceedOperation:completedOperation succeedBlock:succeedBlock];
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

#pragma mark - Private
#pragma mark Helper
- (void)_handleLoginSucceedOperation:(MKNetworkOperation*)completedOperation
                        succeedBlock:(EntitySuccessBlock)succeedBlock {
    NSDictionary* retDict = completedOperation.responseJSON;
    NSDictionary* peopleDict = [retDict dictValueForKeyPath:@"data.people"];
    if (peopleDict) {
        [QSUserManager shareUserManager].userInfo = peopleDict;
        [[NSNotificationCenter defaultCenter] postNotificationName:kUserInfoUpdateNotification object:nil userInfo:nil];
    }
    [QSUserManager shareUserManager].fIsLogined = YES;
    [self userBindCurrentJpushIdOnSucceed:nil onError:nil];
    if (succeedBlock) {
        succeedBlock(peopleDict, [retDict dictValueForKeyPath:@"metadata"]);
    }
}
@end
