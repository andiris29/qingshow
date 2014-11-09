//
//  QSNetworkEngine.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "MKNetworkEngine.h"
#import "QSBlock.h"

#define SHARE_NW_ENGINE [QSNetworkEngine shareNetworkEngine]

@interface QSNetworkEngine : MKNetworkEngine

#pragma mark - Static Method
+ (QSNetworkEngine*)shareNetworkEngine;

#pragma mark - User
- (MKNetworkOperation*)loginWithName:(NSString*)userName
                            password:(NSString*)password
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)logoutOnSucceed:(VoidBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock;

#pragma mark - Feeding
- (MKNetworkOperation*)getChosenFeedingPage:(int)page
                                  onSucceed:(ArraySuccessBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getFeedByModel:(NSString*)modelId
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;
#pragma mark - Model
- (MKNetworkOperation*)getModelListPage:(int)page
                              onSucceed:(ArraySuccessBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;

#pragma mark - Interaction
- (MKNetworkOperation*)handleFollowModel:(NSDictionary*)model
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)followPeople:(NSString*)peopleId
                          onSucceed:(VoidBlock)succeedBlock
                            onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)unfollowPeople:(NSString*)peopleId
                            onSucceed:(VoidBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;
@end
