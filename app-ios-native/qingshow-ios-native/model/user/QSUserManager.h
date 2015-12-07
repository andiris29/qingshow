//
//  QSUserManager.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/17/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSUserManager : NSObject

+ (QSUserManager*)shareUserManager;

@property (strong, nonatomic) NSDictionary* userInfo;
@property (assign, nonatomic) BOOL fIsLogined;

@property (assign, nonatomic) BOOL fShouldShowLoginGuideAfterCreateMatcher;

@property (strong, nonatomic) NSString* weiboAccessToken;
@property (strong, nonatomic) NSString* weiboUserId;


@property (strong, nonatomic) NSDate* globalFirstLaunchShowDueDate;
@property (strong, nonatomic) NSString* globalFirstLaunchTitle;
@property (strong, nonatomic) NSString* configEventImagePath;

@property (strong, nonatomic) NSString* JPushRegistrationID;

@property (strong, nonatomic) NSString* faqContentPath;
@property (strong, nonatomic) NSString* bonusWithdrawImgPath;

@end
