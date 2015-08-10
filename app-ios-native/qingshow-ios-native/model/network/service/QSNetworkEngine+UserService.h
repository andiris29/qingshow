//
//  QSNetworkEngine+UserService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"
#define kUserInfoUpdateNotification @"kUserInfoUpdateNotification"
@interface QSNetworkEngine(UserService)

#pragma mark - User
- (MKNetworkOperation*)loginWithName:(NSString*)userName
                            password:(NSString*)password
                           onSucceed:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)loginViaWeiboAccessToken:(NSString*)accessToken
                                            uid:(NSString*)uid
                                      onSucceed:(EntitySuccessBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)loginViaWechatCode:(NSString*)code
                                onSucceed:(EntitySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)logoutOnSucceed:(VoidBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock;

//- (MKNetworkOperation *)registerById:(NSString *)pid
//                            Password:(NSString *)passwd
//                           onSuccess:(EntitySuccessBlock)succeedBlock
//                             onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation *)registerByNickname:(NSString *)nickName
                                  Password:(NSString *)passwd
                                        Id:(NSString *)pid
                               onSucceessd:(EntitySuccessBlock)successdBlock
                                   onErrer:(ErrorBlock)errorBlock;

- (MKNetworkOperation *)updatePeople:(NSDictionary *)people
                           onSuccess:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation *)updatePortrait:(NSData *)image
                             onSuccess:(EntitySuccessBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation *)updateBackground:(NSData *)image
                               onSuccess:(EntitySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;


- (MKNetworkOperation *)getLoginUserOnSucced:(EntitySuccessBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation *)saveReceiver:(NSString*)uuid
                                name:(NSString*)name
                               phone:(NSString*)phone
                            province:(NSString*)province
                             address:(NSString*)address
                           isDefault:(BOOL)isDefault
                           onSuccess:(void (^)(NSDictionary *people, NSString* uuid, NSDictionary *metadata))succeedBlock
                             onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)setDefaultReceiver:(NSDictionary*)receiverDict
                                onSuccess:(VoidBlock)successBlock
                                  onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)removeReceiver:(NSDictionary*)receiver
                            onSuccess:(VoidBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;
@end
