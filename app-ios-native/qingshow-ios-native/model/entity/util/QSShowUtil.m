//
//  QSShowUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowUtil.h"
#import "QSItemUtil.h"
#import "NSNumber+QSExtension.h"
#import "QSCommonUtil.h"
#import "NSArray+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"
#import "QSDateUtil.h"
@implementation QSShowUtil
+ (NSURL*)getHoriCoverUrl:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = dict[@"horizontalCover"];
    if ([QSCommonUtil checkIsNil:cover]) {
        return [self getCoverUrl:dict];
    } else {
        return [NSURL URLWithString:cover];
    }
}
+ (NSURL*)getCoverUrl:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = dict[@"cover"];
    if ([QSCommonUtil checkIsNil:cover]) {
        return nil;
    }
    return [NSURL URLWithString:cover];
}
+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSArray* posters = dict[@"posters"];
    NSMutableArray* urlArray = [@[] mutableCopy];
    if (posters && posters.count) {
        for (NSString* path in posters) {
            [urlArray addObject:[NSURL URLWithString:path]];
        }
        return urlArray;
    }
    else {
        return nil;
    }
}

+ (NSArray*)getItemsImageUrlArrayFromShow:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSArray* itemArray = dict[@"itemRefs"];
    return [QSItemUtil getItemsImageUrlArray:itemArray];
}
+ (NSArray*)getItems:(NSDictionary *)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    NSArray* itemArray = showDict[@"itemRefs"];
    if (itemArray.count) {
        if (![itemArray[0] isKindOfClass:[NSDictionary class]]) {
            return @[];
        }
    }
    return itemArray;
}

+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    if (![showDict isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    NSArray* items = [self getItems:showDict];
    if (items) {
        return items[index];
    }
    
    return nil;
}

+ (NSDictionary*)getCoverMetadata:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    if (showDict) {
        return showDict[@"coverMetadata"];
    }
    return nil;
}

+ (NSString*)getNumberCommentsDescription:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    NSDictionary* context = showDict[@"__context"];
    if (context) {
        return ((NSNumber*)context[@"numComments"]).kmbtStringValue;
    }
    return @"0";
}
+ (void)addNumberComment:(long long)num forShow:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict] || ![showDict isKindOfClass:[NSMutableDictionary class]]) {
        return;
    }
    NSMutableDictionary* m = (NSMutableDictionary*)showDict;
    NSMutableDictionary* context = [showDict[@"__context"] mutableCopy];
    if (context) {
        if ([QSCommonUtil checkIsNil:context[@"numComments"]]) {
            return;
        }
        context[@"numComments"] = @(((NSNumber*)context[@"numComments"]).longLongValue + num);
        m[@"__context"] = context;
    }
}

+ (NSString*)getNumberLikeDescription:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    return ((NSNumber*)showDict[@"numLike"]).kmbtStringValue;
}

+ (NSString*)getNumberItemDescription:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    return @([self getItems:showDict].count).kmbtStringValue;
}

+ (BOOL)getIsLike:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return NO;
    }
    NSDictionary* context = showDict[@"__context"];
    if (context) {
        return ((NSNumber*)context[@"likedByCurrentUser"]).boolValue;
    }
    return NO;
}

+ (void)setIsLike:(BOOL)isLike show:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return;
    }
    if ([showDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)showDict;
        NSDictionary* context = showDict[@"__context"];
        NSMutableDictionary* m = nil;
        if ([context isKindOfClass:[NSDictionary class]]) {
            m = [context mutableCopy];
        } else
        {
            m = [@{} mutableCopy];
        }
        m[@"likedByCurrentUser"] = @(isLike);
        s[@"__context"] = m;
    }
}

+ (void)addNumberLike:(long long)num forShow:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return;
    }
    if ([showDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)showDict;
        long long preNumlike = ((NSNumber*)s[@"numLike"]).longLongValue;
        s[@"numLike"] = @(preNumlike + num);
    }
}

+ (NSDate*)getRecommendDate:(NSDictionary*)showDict
{
    if (![QSCommonUtil checkIsDict:showDict]) {
        return nil;
    }
    NSDictionary* rec = showDict[@"recommend"];
    if (![QSCommonUtil checkIsDict:rec]) {
        return nil;
    }
    id date = rec[@"date"];
    
    if ([QSCommonUtil checkIsNil:date]) {
        return nil;
    } else {
        if ([date isKindOfClass:[NSDate class]]) {
            return date;
        } else {
            NSString* dateStr = date;
            return [QSDateUtil buildDateFromResponseString:dateStr];
        }
    }
}

+ (NSString*)getRecommentDesc:(NSDictionary*)showDict
{
    if (![QSCommonUtil checkIsDict:showDict]) {
        return nil;
    }
    NSDictionary* rec = showDict[@"recommend"];
    if (![QSCommonUtil checkIsDict:rec]) {
        return nil;
    }
    return rec[@"description"];
}
@end
