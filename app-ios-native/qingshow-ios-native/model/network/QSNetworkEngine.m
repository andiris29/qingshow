//
//  QSNetworkEngine.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//


#import "QSNetworkEngine.h"
#import "QSNetworkEngine+Protect.h"

#import "ServerPath.h"
#import "QSNetworkOperation.h"
#import "QSUserManager.h"
#import "QSFeedingCategory.h"
#import "NSArray+QSExtension.h"
#import "QSPeopleUtil.h"
#import "QSShowUtil.h"
#import "QSHotUtil.h"

@implementation QSNetworkEngine

#pragma mark - Static Method
+ (QSNetworkEngine*)shareNetworkEngine
{
    static QSNetworkEngine* s_networkEngine = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_networkEngine = [[QSNetworkEngine alloc] initWithHostName:PATH_SERVER_ADDR_SERVER];
        [s_networkEngine registerOperationSubclass:[QSNetworkOperation class]];
        [NSHTTPCookieStorage sharedHTTPCookieStorage].cookieAcceptPolicy = NSHTTPCookieAcceptPolicyAlways;
        
    });
    return s_networkEngine;
}
+ (QSNetworkEngine*)shareNetworkEngineForAPPDelegate
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

@end
