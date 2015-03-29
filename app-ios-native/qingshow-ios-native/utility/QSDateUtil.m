//
//  QSDateUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSDateUtil.h"
#import "QSCommonUtil.h"
@implementation QSDateUtil

+ (NSDate*)buildDateFromResponseString:(NSString*)str
{
    if ([QSCommonUtil checkIsNil:str]) {
        return nil;
    }
    NSMutableString* dateStr = [str mutableCopy];
    //2014-10-16T13:42:54.021Z
    NSDate* date = nil;
    if (dateStr) {
        //        int dotIndex =
        NSRange dotRange = [dateStr rangeOfString:@"."];
        NSRange removeRange = NSMakeRange(dotRange.location, dateStr.length - dotRange.location);
        if (removeRange.location != NSNotFound) {
            [dateStr deleteCharactersInRange:removeRange];
        }
        
        NSRange tRange = [dateStr rangeOfString:@"T"];
        if (tRange.location != NSNotFound) {
            [dateStr replaceCharactersInRange:NSMakeRange(tRange.location, 1) withString:@" "];
        }
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        [dateFormatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
        date = [dateFormatter dateFromString:dateStr];
        return date;
    }
    
    return nil;
}
+ (NSString*)buildStringFromDate:(NSDate*)date
{
    if (date)
    {
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        NSString* currentDateStr = [dateFormatter stringFromDate:date];
        [dateFormatter setTimeZone:[NSTimeZone defaultTimeZone]];
        return currentDateStr;
    }
    else
    {
        return nil;
    }
}
+ (NSString*)buildDateStringFromDate:(NSDate*)date
{
    if (date)
    {
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd"];
        NSString* currentDateStr = [dateFormatter stringFromDate:date];
        [dateFormatter setTimeZone:[NSTimeZone defaultTimeZone]];
        return currentDateStr;
    }
    else
    {
        return nil;
    }
}

+ (NSString*)getTime:(NSDate*)date
{
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c = [calendar components:(NSHourCalendarUnit | NSMinuteCalendarUnit) fromDate:date];
    return [NSString stringWithFormat:@"%d:%d",(int)c.hour, (int)c.minute];
}
+ (NSString*)getMYD:(NSDate*)date
{
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c = [calendar components:(NSDayCalendarUnit | NSMonthCalendarUnit| NSYearCalendarUnit) fromDate:date];
    
    return [NSString stringWithFormat:@"%d/%d/%d", (int)c.year, (int)c.month, (int)c.day];
}
+ (NSString*)getWeek:(NSDate*)date
{
    NSArray* a = @[@"",
                   @"星期日SUN",
                   @"星期一MON",
                   @"星期二TUE",
                   @"星期三WED",
                   @"星期四THU",
                   @"星期五FRI",
                   @"星期六SAT",];
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSInteger week = [calendar components:NSWeekdayCalendarUnit fromDate:date].weekday;

    return a[week];
}
@end
