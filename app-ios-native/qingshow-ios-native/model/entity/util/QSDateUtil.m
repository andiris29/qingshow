//
//  QSDateUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSDateUtil.h"

@implementation QSDateUtil

+ (NSDate*)buildDateFromResponseString:(NSString*)str
{
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
    return [NSString stringWithFormat:@"%d:%d",c.hour, c.minute];
}
+ (NSString*)getMYD:(NSDate*)date
{
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c = [calendar components:(NSDayCalendarUnit | NSMonthCalendarUnit| NSYearCalendarUnit) fromDate:date];
    
    return [NSString stringWithFormat:@"%d/%d/%d", c.year, c.month, c.day];
}
+ (NSString*)getWeek:(NSDate*)date
{
    NSArray* a = @[
                   @"星期二",
                   @"星期三",
                   @"星期四",
                   @"星期五",
                   @"星期六",
                   @"星期日",
                   @"星期一"];
    NSCalendar *calendar = [NSCalendar currentCalendar];
    int week = [calendar component:NSWeekdayCalendarUnit fromDate:date];

    return a[week];
}
@end
