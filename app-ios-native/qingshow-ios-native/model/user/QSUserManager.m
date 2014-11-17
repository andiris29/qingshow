//
//  QSUserManager.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/17/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSUserManager.h"

@interface QSUserManager ()

@property (strong, nonatomic) NSUserDefaults* userDefault;

@end

@implementation QSUserManager
+ (QSUserManager*)shareUserManager
{
    static QSUserManager* s_userManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_userManager = [[QSUserManager alloc] init];
        
    });
    return s_userManager;
}
@end
