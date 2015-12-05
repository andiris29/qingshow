//
//  QSDateUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.`
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSDateUtil : NSObject

+ (NSDate*)clearTimeFromDate:(NSDate*)date;
+ (NSDate*)buildDateFromResponseString:(NSString*)str;
+ (NSString*)buildStringFromDate:(NSDate*)date;
+ (NSString*)getTime:(NSDate*)date;
+ (NSString*)getMYD:(NSDate*)date;
+ (NSString*)getWeek:(NSDate*)date;
+ (NSString*)gettimeSinceDate:(NSDate *)date;

+ (NSString*)buildDateStringFromDate:(NSDate*)date;
+ (NSString*)buildDayStringFromDate:(NSDate*)date;
+ (BOOL)date:(NSDate*)date1 isTheSameDayWith:(NSDate*)date2;
+ (NSString*)getDayDesc:(NSDate*)date;
+ (NSString*)getMonthDesc:(NSDate*)date;
+ (NSString*)getYearDesc:(NSDate*)date;
+ (NSString*)getWeekdayDesc:(NSDate*)date;
+ (NSInteger)getWeekdayIndex:(NSDate*)date;
+ (NSInteger)getHourNumber:(NSDate*)date;

//获取日期
+ (NSString*)getMonthAndDate:(NSDate*)date;
+ (NSString*)buildDotStringFromDate:(NSDate*)date;
@end
