//
//  QSError.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/9/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#define kQSErrorDomain @"qs_error_domain"
#define kQSErrorCodeNumber 1000

#define kQSErrorCodePageNotExist @"ERR_PAGE_NOT_EXISTS"
#define kQSErrorCodeAlreadyFollow @"ERR_ALREADY_FOLLOW"
#define kQSErrorCodeAlreadyUnfollow @"ERR_ALREADY_UNFOLLOW"
@interface QSError : NSError

@property (copy, nonatomic) NSString* qsErrorCode;
@property (copy, nonatomic) NSString* qsErrorMessage;
- (NSString*)toString;

@end
