//
//  QSExtension.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "NSDate+QSExtension.h"

@implementation NSDate (QSExtension)

- (BOOL)isToday
{
    NSDate *now = [NSDate date];
    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    fmt.dateFormat = @"yyyy-MM-dd";
    NSString *dateStr = [fmt stringFromDate:self];
    NSString *nowStr = [fmt stringFromDate:now];
    return [dateStr isEqualToString:nowStr];

}
- (BOOL)isYesterday
{
    NSDate *now = [NSDate date];
    NSDate *date = self;
    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    fmt.dateFormat = @"yyyy-MM-dd";
    
    NSString *dateStr = [fmt stringFromDate:date];
    NSString *nowStr = [fmt stringFromDate:now];
    date = [fmt dateFromString:dateStr];
    now = [fmt dateFromString:nowStr];
    
    NSCalendar *calendar = [NSCalendar currentCalendar];
    
    NSCalendarUnit unit = NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay;
    NSDateComponents *cmps = [calendar components:unit fromDate:date toDate:now options:0];
    return cmps.year == 0 && cmps.month == 0 && cmps.day == 1;
    
}

- (NSDateComponents *)deltaWithNow
{
    NSCalendar *calendar = [NSCalendar currentCalendar];
    int unit = NSCalendarUnitHour | NSCalendarUnitMinute | NSCalendarUnitSecond;
    
    return [calendar components:unit fromDate:self toDate:[NSDate date] options:0];
}

@end
