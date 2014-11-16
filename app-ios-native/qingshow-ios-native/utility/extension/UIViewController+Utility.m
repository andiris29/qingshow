//
//  UIViewController+Utility.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/15.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "UIViewController+Utility.h"

@implementation UIViewController (Utility)

# pragma mark - Validation

- (BOOL) checkString:(NSString *)target ByPattern:(NSString *)pattern {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", pattern];
    return [predicate evaluateWithObject:target];
}

- (BOOL) checkEmail:(NSString *)email {
    return [self checkString:email ByPattern:EMAIL_PATTERN];
}

- (BOOL)checkPasswd:(NSString *)passwd {
    return [self checkString:passwd ByPattern:PASSWD_PATTERN];
}

@end
