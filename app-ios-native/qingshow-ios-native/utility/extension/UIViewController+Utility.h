//
//  UIViewController+Utility.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/15.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define EMAIL_PATTERN @"^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$"
#define PASSWD_PATTERN @"^[A-Za-z0-9]{6,99}$"
#define PHONE_PATTERN @"^((13[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$"

@interface UIViewController (Utility)

#pragma mark - Validation

- (BOOL) checkString:(NSString *)target ByPattern:(NSString *)pattern;
- (BOOL) checkEmail:(NSString *)email;
- (BOOL) checkPasswd:(NSString *)passwd;
- (BOOL) checkMobile:(NSString *)mobile;

@end
