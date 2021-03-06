//
//  QSNetworkEngine.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//


#import "QSNetworkEngine.h"
#import "MKNetworkEngine+QSExtension.h"

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
static QSNetworkEngine* s_networkEngine = nil;
+ (void)hostInit:(NSString*)hostPath {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSString* path = hostPath;
        if (!path) {
            path = @"";
        }
        s_networkEngine = [[QSNetworkEngine alloc] initWithHostName:[path stringByReplacingOccurrencesOfString:@"http://" withString:@""]];
        [s_networkEngine registerOperationSubclass:[QSNetworkOperation class]];
    });
}

+ (QSNetworkEngine*)shareNetworkEngine
{
    return s_networkEngine;
}

@end
