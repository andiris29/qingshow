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

+ (QSNetworkEngine*)shareNetworkEngine;
- (MKNetworkOperation*)loginWithName:(NSString*)userName
                            password:(NSString*)password
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)logoutOnSucceed:(VoidBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock;

@end
