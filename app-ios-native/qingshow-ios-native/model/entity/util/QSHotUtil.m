//
//  QSHotUtil.m
//  qingshow-ios-native
//
//  Created by Han Hugh on 15/5/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSHotUtil.h"
#import "QSEntityUtil.h"
#import "NSNumber+QSExtension.h"
#import "QSDateUtil.h"

@implementation QSHotUtil

+ (NSURL *)getHotCoverUrl:(NSDictionary *)topShows
{
    if ([QSEntityUtil checkIsNil:topShows]) {
        return nil;
    }
    NSString *cover = topShows[@"cover"];
    if ([QSEntityUtil checkIsNil:cover]) {
        return [self getHotCoverUrl:topShows];
    } else {
        return [NSURL URLWithString:cover];
    }
}
+ (NSString *)getHotNumLike:(NSDictionary *)topShows
{
    if ([QSEntityUtil checkIsNil:topShows]) {
        return nil;
    }
    return ((NSNumber *)topShows[@"numLike"]).kmbtStringValue;
}
+ (NSDate *)getHotCreateDate:(NSDictionary *)topShows
{
    if (![QSEntityUtil checkIsDict:topShows]) {
        return nil;
    }
    NSString *dateStr = topShows[@"create"];
    return [QSDateUtil buildDateFromResponseString:dateStr];
  }
+ (NSDate *)getHotUpDate:(NSDictionary *)topShows
{
    if (![QSEntityUtil checkIsNil:topShows]) {
        return nil;
    }
    NSString *upDateStr = topShows[@"update"];
    return [QSDateUtil buildDateFromResponseString:upDateStr];
         }

@end
