//
//  QSError.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/9/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#define kQSErrorDomain @"qs_error_domain"

@interface QSError : NSError

- (NSString*)toString;

@end
