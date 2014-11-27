//
//  QSDateUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.`
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSDateUtil : NSObject

+ (NSDate*)buildDateFromResponseString:(NSString*)str;
+ (NSString*)buildStringFromDate:(NSDate*)date;
+ (NSString*)getTime:(NSDate*)date;
+ (NSString*)getMYD:(NSDate*)date;
+ (NSString*)getWeek:(NSDate*)date;

@end
