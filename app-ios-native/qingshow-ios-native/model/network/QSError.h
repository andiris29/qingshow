//
//  QSError.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/9/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#define kQSErrorDomain @"qs_error_domain"

typedef NS_ENUM(NSInteger, kQSErrorCode) {
    kQSErrorCodePageNotExist = 1009,
    kQSErrorCodeAlreadyFollow = 1019,
    kQSErrorCodeAlreadyUnfollow = 1020
};

@interface QSError : NSError

- (NSString*)toString;

@end
