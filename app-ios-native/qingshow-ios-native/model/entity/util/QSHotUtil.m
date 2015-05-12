//
//  QSHotUtil.m
//  qingshow-ios-native
//
//  Created by Han Hugh on 15/5/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSHotUtil.h"
#import "QSCommonUtil.h"
#import "NSNumber+QSExtension.h"
#import "QSDateUtil.h"

@implementation QSHotUtil

+ (NSURL *)getHotCoverUrl:(NSDictionary *)topShows
{
    if ([QSCommonUtil checkIsNil:topShows]) {
        return nil;
    }
    NSString *cover = topShows[@"cover"];
    if ([QSCommonUtil checkIsNil:cover]) {
        return [self getHotCoverUrl:topShows];
    } else {
        return [NSURL URLWithString:cover];
    }
}
+ (NSString *)getHotNumLike:(NSDictionary *)topShows
{
    if ([QSCommonUtil checkIsNil:topShows]) {
        return nil;
    }
    return ((NSNumber *)topShows[@"numLike"]).kmbtStringValue;
}
+ (NSDate *)getHotCreateDate:(NSDictionary *)topShows
{
    if ([QSCommonUtil checkIsNil:nil]) {
        return nil;
    }
    id createDate = topShows[@"create"];
    if ([QSCommonUtil checkIsNil:createDate]) {
        return nil;
    }else {
        if ([createDate isKindOfClass:[NSDate class]]) {
            return createDate;
        }else {
            NSString *dateStr = createDate;
            return [QSDateUtil buildDateFromResponseString:dateStr];
        }
    }
}
+ (NSDate *)getHotUpDate:(NSDictionary *)topShows
{
    if ([QSCommonUtil checkIsNil:topShows]) {
        return nil;
    } else {
        id upDate = topShows[@"upDate"];
        if ([upDate isKindOfClass:[NSDate class]]) {
            return upDate;
        } else {
            NSString *upDateStr = upDate;
            return [QSDateUtil buildDateFromResponseString:upDateStr];
        }
    }
}

@end
