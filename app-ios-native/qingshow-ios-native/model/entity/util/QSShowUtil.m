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

@implementation QSShowUtil
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
        if (![itemArray[0] isKindOfClass:[NSDictionary class]]   ) {
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
    NSArray* items = showDict[@"itemRefs"];
    if (items) {
        return items[index];
    }
    
    return nil;
}
+ (NSDictionary*)getPeopleFromShow:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    if (showDict) {
        return showDict[@"modelRef"];
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
+ (NSString*)getNumberLikeDescription:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    NSDictionary* context = showDict[@"__context"];
    if (context) {
        return ((NSNumber*)context[@"numLike"]).kmbtStringValue;
    }
    return @"0";
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
        NSDictionary* context = showDict[@"__context"];
        NSMutableDictionary* m = nil;
        if ([context isKindOfClass:[NSDictionary class]]) {
            m = [context mutableCopy];
        } else
        {
            m = [@{} mutableCopy];
        }
        long long preNumlike = ((NSNumber*)m[@"numLike"]).longLongValue;
        
        m[@"numLike"] = @(preNumlike + num);
        s[@"__context"] = m;
    }
}
@end
