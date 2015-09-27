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
#define PATH_USER_LOGIN_WEIBO @"user/loginViaWeibo"
#define PATH_USER_LOGIN_WECHAT @"user/loginViaWeixin"

#define PATH_USER_LOGOUT @"user/logout"
#define PATH_USER_GET @"user/get"
#define PATH_USER_REGISTER @"user/register"
#define PATH_USER_UPDATE @"user/update"
#define PATH_USER_UPDATE_PORTRAIT @"user/updatePortrait"
#define PATH_USER_UPDATE_BACKGROUND @"user/updateBackground"
#define PATH_USER_SAVE_RECEIVER @"user/saveReceiver"
#define PATH_USER_REMOVE_RECEIVER @"user/removeReceiver"
#define PATH_USER_BONUS_WITHDRAW @"userBonus/withdraw"
#define PATH_MOBILE_REQUEST_CODE @"user/requestVerificationCode"
#define PATH_MOBILE_VALIDATE @"user/validateMobile"



@implementation QSNetworkEngine(UserService)


#pragma mark - User
- (MKNetworkOperation*)loginWithName:(NSString*)userName
                            password:(NSString*)password
                           onSucceed:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{
                                        @"idOrNickName" : userName,
                                        @"password" : password
                                        } mutableCopy];
    if ([QSUserManager shareUserManager].JPushRegistrationID) {
        paramDict[@"registrationId"] = [QSUserManager shareUserManager].JPushRegistrationID;
    }
    
    return [self startOperationWithPath:PATH_USER_LOGIN
                                 method:@"POST"
                               paramers:paramDict
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


- (MKNetworkOperation*)loginViaWeiboAccessToken:(NSString*)accessToken
                                            uid:(NSString*)uid
                                      onSucceed:(EntitySuccessBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock
{
    
    NSMutableDictionary* paramDict = [@{
                                        @"access_token" : accessToken,
                                        @"uid" : uid
                                        } mutableCopy];
    if ([QSUserManager shareUserManager].JPushRegistrationID) {
        paramDict[@"registrationId"] = [QSUserManager shareUserManager].JPushRegistrationID;
    }
    
    return [self startOperationWithPath:PATH_USER_LOGIN_WEIBO
                                 method:@"POST"
                               paramers:paramDict
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

- (MKNetworkOperation*)loginViaWechatCode:(NSString*)code
                                      onSucceed:(EntitySuccessBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock
{
    
    NSMutableDictionary* paramDict = [@{
                                        @"code" : code
                                        } mutableCopy];
    if ([QSUserManager shareUserManager].JPushRegistrationID) {
        paramDict[@"registrationId"] = [QSUserManager shareUserManager].JPushRegistrationID;
    }
    
    return [self startOperationWithPath:PATH_USER_LOGIN_WECHAT
                                 method:@"POST"
                               paramers:paramDict
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
    NSMutableDictionary* paramDict = [@{} mutableCopy];
    if ([QSUserManager shareUserManager].JPushRegistrationID) {
        paramDict[@"registrationId"] = [QSUserManager shareUserManager].JPushRegistrationID;
    }
    
    return [self startOperationWithPath:PATH_USER_LOGOUT
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* userInfo = [QSUserManager shareUserManager].userInfo;
                if (succeedBlock) {
                    if (userInfo && [QSUserManager shareUserManager].userInfo == userInfo) {
                        [QSUserManager shareUserManager].userInfo = nil;
                        [QSUserManager shareUserManager].fIsLogined = NO;
                    }

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
                [[NSNotificationCenter defaultCenter] postNotificationName:kUserInfoUpdateNotification object:manager.userInfo];
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

//- (MKNetworkOperation *)registerById:(NSString *) pid
//                            Password:(NSString *)passwd
//                           onSuccess:(EntitySuccessBlock)succeedBlock
//                             onError:(ErrorBlock)errorBlock {
//    
//    return [self startOperationWithPath:PATH_USER_REGISTER
//                                 method:@"POST"
//                               paramers:@{@"id" : pid, @"password": passwd}
//                            onSucceeded:
//            ^(MKNetworkOperation *completeOperation) {
//                NSDictionary *retDict = completeOperation.responseJSON;
//                [QSUserManager shareUserManager].fIsLogined = YES;
//                if (succeedBlock) {
//                    succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
//                }
//            }
//                                onError:
//            ^(MKNetworkOperation *completedOperation, NSError *error) {
//                if(errorBlock) {
//                    errorBlock(error);
//                }
//            }
//            ];
//}

- (MKNetworkOperation *)registerByNickname:(NSString *)nickName
                                  Password:(NSString *)passwd Id:(NSString *)pid onSucceessd:(EntitySuccessBlock)successdBlock onErrer:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{@"nickname" : nickName, @"password": passwd, @"id":pid} mutableCopy];
    if ([QSUserManager shareUserManager].JPushRegistrationID) {
        paramDict[@"registrationId"] = [QSUserManager shareUserManager].JPushRegistrationID;
    }
    
    return [self startOperationWithPath:PATH_USER_REGISTER
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                [QSUserManager shareUserManager].fIsLogined = YES;
                [QSUserManager shareUserManager].userInfo = retDict[@"data"][@"people"];
                if (successdBlock) {
                    successdBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
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
- (MKNetworkOperation *)registerByNickname:(NSString *)nickName
                                  Password:(NSString *)passwd
                                        Id:(NSString *)pid
                                    mobile:(NSString *)mobileNum
                                      code:(NSString *)code
                               onSucceessd:(EntitySuccessBlock)successdBlock
                                   onErrer:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{@"nickname" : nickName, @"password": passwd, @"id":pid
                                        ,@"mobile":mobileNum,@"verificationCode":code} mutableCopy];
    if ([QSUserManager shareUserManager].JPushRegistrationID) {
        paramDict[@"registrationId"] = [QSUserManager shareUserManager].JPushRegistrationID;
    }
    
    return [self startOperationWithPath:PATH_USER_REGISTER
                                 method:@"POST"
                               paramers:paramDict
                            onSucceeded:
            ^(MKNetworkOperation *completeOperation) {
                NSDictionary *retDict = completeOperation.responseJSON;
                [QSUserManager shareUserManager].fIsLogined = YES;
                [QSUserManager shareUserManager].userInfo = retDict[@"data"][@"people"];
                if (successdBlock) {
                    successdBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
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
- (MKNetworkOperation*)getBonusWithAlipayId:(NSString*)alipayId
                                OnSusscee:(VoidBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_USER_BONUS_WITHDRAW method:@"POST" paramers:@{@"alipayId":alipayId} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)getTestNumberWithMobileNumber:(NSString*)mobileNum
                                           onSucceed:(VoidBlock)succeedBlock
                                             onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_MOBILE_REQUEST_CODE method:@"POST" paramers:@{@"mobileNumber":mobileNum} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
- (MKNetworkOperation*)MobileNumberAvilable:(NSString*)mobileNum
                                       code:(NSString*)code
                                  onSucceed:(BoolBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_MOBILE_VALIDATE method:@"POST" paramers:@{@"mobile":mobileNum,   @"verificationCode":code} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary *retDic = completedOperation.responseJSON;
        if (succeedBlock) {
            succeedBlock((BOOL)retDic[@"data"][@"success"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

@end
