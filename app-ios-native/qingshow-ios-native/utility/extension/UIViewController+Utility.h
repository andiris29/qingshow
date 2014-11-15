//
//  UIViewController+Utility.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/15.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define EMAIL_PATTERN @"^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$"
#define PASSWD_PATTERN @"^[A-Za-z0-9]{8,12}$"

@interface UIViewController (Utility)

#pragma mark - Validation

- (BOOL) checkString:(NSString *)target ByPattern:(NSString *)pattern;
- (BOOL) checkEmail:(NSString *)email;
- (BOOL) checkPasswd:(NSString *)passwd;

@end
