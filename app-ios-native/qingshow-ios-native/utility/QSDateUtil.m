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
        if ([QSCommonUtil checkIsNil:str]) {
            return nil;
        } else {
            return date;
        }
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
        [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        return currentDateStr;
    }
    else
    {
        return nil;
    }
}
+ (NSString*)buildDayStringFromDate:(NSDate*)date {
    if (date)
    {
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy.MM.dd"];
        NSString* currentDateStr = [dateFormatter stringFromDate:date];
        [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
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
        [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        return currentDateStr;
    }
    else
    {
        return nil;
    }
}

+ (NSString*)getTime:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"HH:mm"];
    [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
    return [dateFormatter stringFromDate:date];
}

+ (NSString *)gettimeSinceDate:(NSString *)date
{
    if (!date) {
        return nil;
    }
    NSDate *nowDate = [NSDate date];
//    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
//    [dateFormatter setDateFormat:@"yyyy-MM-dd hh:mm:ss"];
    NSDate *date01 = [self buildDateFromResponseString:date];
    NSTimeInterval seconds = [nowDate timeIntervalSinceDate:date01];
    if (seconds >= 24*60*60) {
        return [NSString stringWithFormat:@"%d天之前",(int)seconds/(24*60*60)];
    }
    else if(seconds >= 60*60)
    {
        return [NSString stringWithFormat:@"%d小时之前",(int)seconds/(60*60)];
    }
    else if (seconds >= 60)
    {
        return [NSString stringWithFormat:@"%d分钟之前",(int)seconds/60];
    }
    else
    {
        return [NSString stringWithFormat:@"%d秒之前",(int)seconds];
    }
    return nil;
    
}
+ (NSString*)getMYD:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c = [calendar components:(NSCalendarUnitDay | NSCalendarUnitMonth| NSCalendarUnitYear) fromDate:date];
    
    return [NSString stringWithFormat:@"%d/%d/%d", (int)c.year, (int)c.month, (int)c.day];
}
//获取日期
+ (NSString*)getMonthAndDate:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"MM/dd"];
    [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
    return [dateFormatter stringFromDate:date];
    
}
+ (NSString*)getWeek:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    NSArray* a = @[@"",
                   @"星期日SUN",
                   @"星期一MON",
                   @"星期二TUE",
                   @"星期三WED",
                   @"星期四THU",
                   @"星期五FRI",
                   @"星期六SAT",];
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSInteger week = [calendar components:NSCalendarUnitWeekday fromDate:date].weekday;

    return a[week];
}

+ (BOOL)date:(NSDate*)date1 isTheSameDayWith:(NSDate*)date2
{
    if (!date1 || !date2) {
        return date1 == date2;
    }
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c1 = [calendar components:(NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear) fromDate:date1];
    NSDateComponents* c2 = [calendar components:(NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear) fromDate:date2];
    return c1.year == c2.year && c1.month == c2.month && c1.day == c2.day;
}

+ (NSString*)getDayDesc:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c = [calendar components:(NSCalendarUnitDay) fromDate:date];
    return @(c.day).stringValue;
}

+ (NSString*)getMonthDesc:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    NSArray* monthNames = @[
                            @"",
                            @"Jan",
                            @"Feb",
                            @"Mar",
                            @"Apr",
                            @"May",
                            @"Jun",
                            @"Jul",
                            @"Aug",
                            @"Sep",
                            @"Oct",
                            @"Nov",
                            @"Dec"];
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c = [calendar components:(NSCalendarUnitMonth) fromDate:date];
    return monthNames[c.month];
}

+ (NSString*)getYearDesc:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents* c = [calendar components:(NSCalendarUnitYear) fromDate:date];
    return @(c.year).stringValue;
}
+ (int)getWeekdayIndex:(NSDate*)date
{
    if ([QSCommonUtil checkIsNil:date]) {
        return 0;
    } else {
        NSCalendar *calendar = [NSCalendar currentCalendar];
        NSDateComponents* c = [calendar components:(NSCalendarUnitWeekday) fromDate:date];
        return c.weekday;
    }
}
+ (NSString*)getWeekdayDesc:(NSDate*)date
{
    if (!date) {
        return nil;
    }
    NSArray* array = @[
                       @"",
                       @"SUNDAY",
                       @"MONDAY",
                       @"TUESDAY",
                       @"WEDNESDAY",
                       @"THURSDAY",
                       @"FRIDAY",
                       @"SATURDAY"];
    return array[[self getWeekdayIndex:date]];
}
@end
