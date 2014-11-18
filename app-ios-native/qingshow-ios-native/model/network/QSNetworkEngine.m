//
//  QSNetworkEngine.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine.h"
#import "ServerPath.h"
#import "QSNetworkOperation.h"
#import "QSUserManager.h"

//User
#define PATH_USER_LOGIN @"user/login"
#define PATH_USER_LOGOUT @"user/logout"
#define PATH_USER_GET @"user/get"
#define PATH_USER_REGISTER @"user/register"

//Model
#define PATH_QUERY_MODELS @"query/models"
#define PATH_INTERACTION_FOLLOW @"interaction/follow"
#define PATH_INTERACTION_UNFOLLOW @"interaction/unfollow"

//Feeding
#define PATH_FEEDING_CHOSEN @"feeding/chosen"
#define PATH_FEEDING_BY_MODEL @"feeding/byModel"

//Query

#define Interaction



@implementation QSNetworkEngine

#pragma mark - Static Method
+ (QSNetworkEngine*)shareNetworkEngine
{
    static QSNetworkEngine* s_networkEngine = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_networkEngine = [[QSNetworkEngine alloc] initWithHostName:HOST_NAME];
        [s_networkEngine registerOperationSubclass:[QSNetworkOperation class]];
        [NSHTTPCookieStorage sharedHTTPCookieStorage].cookieAcceptPolicy = NSHTTPCookieAcceptPolicyAlways;

    });
    return s_networkEngine;
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
                           onSucceed:(EntitySuccessBlock)succeedBlock
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
                    NSDictionary *reDict = completedOperation.responseJSON;
                    [QSUserManager shareUserManager].userInfo = reDict[@"data"][@"people"];
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

#pragma mark - Feeding
- (MKNetworkOperation*)getChosenFeedingPage:(int)page
                                  onSucceed:(ArraySuccessBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_FEEDING_CHOSEN
                                 method:@"GET"
                               paramers:@{@"pageNo" : @(page),
                                          @"pageSize" : @10}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"shows"], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation*)getFeedByModel:(NSString*)modelId
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_FEEDING_BY_MODEL
                                 method:@"GET"
                               paramers:@{@"_id" : modelId,
                                          @"pageNo" : @(page),
                                          @"pageSize" : @10}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"shows"], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}



#pragma mark - Model
- (MKNetworkOperation*)getModelListPage:(int)page
                              onSucceed:(ArraySuccessBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_QUERY_MODELS
                                 method:@"GET"
                               paramers:@{@"pageNo" : @(page),
                                          @"paegSize" : @10}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"peoples"], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

#pragma mark - Interaction
- (MKNetworkOperation*)handleFollowModel:(NSDictionary*)model
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    NSNumber* hasFollowed = model[@"hasFollowed"];
    NSString* modelId = model[@"_id"];
    if (hasFollowed && hasFollowed.boolValue) {
        return [self unfollowPeople:modelId onSucceed:^{
            if (succeedBlock) {
                succeedBlock(NO);
            }
        } onError:errorBlock];
    }
    else
    {
        return [self followPeople:modelId onSucceed:^{
            if (succeedBlock) {
                succeedBlock(YES);
            }
        } onError:errorBlock];
    }
}
- (MKNetworkOperation*)followPeople:(NSString*)peopleId
                          onSucceed:(VoidBlock)succeedBlock
                            onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_INTERACTION_FOLLOW
                                 method:@"POST" paramers:@{@"_id" : peopleId}
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

- (MKNetworkOperation*)unfollowPeople:(NSString*)peopleId
                            onSucceed:(VoidBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_INTERACTION_UNFOLLOW
                                 method:@"POST"
                               paramers:@{@"_id" : peopleId}
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

- (MKNetworkOperation *)getLoginUserOnSucced:(EntitySuccessBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_USER_GET
                                 method:@"GET"
                               paramers:nil
                            onSucceeded:
                                ^(MKNetworkOperation *completeOperation) {
                                    NSDictionary* retDict = completeOperation.responseJSON;
                                    if (succeedBlock) {
                                        succeedBlock(retDict[@"data"][@"people"], retDict[@"metadata"]);
                                    }
                                }
                                onError:
                                    ^(MKNetworkOperation *completedOperation, NSError *error) {
                                        if (errorBlock) {
                                            errorBlock(error);
                                        }
                                    }
            ];
}

- (MKNetworkOperation *)registerByMail:(NSString *)mail
                              Password:(NSString *)passwd
                             onSuccess:(EntitySuccessBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock {
    
    return [self startOperationWithPath:PATH_USER_REGISTER
                                 method:@"POST"
                               paramers:@{@"mail" : mail, @"encryptedPassword": passwd}
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
