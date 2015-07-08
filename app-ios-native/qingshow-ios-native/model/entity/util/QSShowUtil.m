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
//新增获取高度的实现代码
+ (CGFloat)getCoverMetaDataHeight:(NSDictionary *)dic
{
    if ([QSCommonUtil checkIsNil:dic]) {
        return 180;
    }
    if ([QSCommonUtil checkIsNil:dic[@"coverMetaData.height"]]) {
        return 180;
    }
    else{
        return  [dic[@"coverMetaData.height"] floatValue];
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

+ (NSURL*)getCoverBackgroundUrl:(NSDictionary*)dict {
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = [dict valueForKeyPath:@"coverInfo.background"];
    if ([QSCommonUtil checkIsNil:cover]) {
        return nil;
    }
    return [NSURL URLWithString:cover];
}

+ (NSURL*)getCoverForegroundUrl:(NSDictionary*)dict {
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = [dict valueForKey:@"coverForeground"];
    
    if ([QSCommonUtil checkIsNil:cover]) {
        return nil;
    }
    return [NSURL URLWithString:cover];
}

+ (NSString *)getNameStr:(NSDictionary *)dict
{
    NSDictionary *nameDic = [dict valueForKey:@"__context"];
    NSDictionary *createDic = nameDic[@"createdBy"];
    if (!createDic) {
        return nil;
    }
    return createDic[@"nickname"];
}
+ (NSString *)getUserId:(NSDictionary *)dict
{
    NSString  *userId  = dict[@"id"];
    if ([QSCommonUtil checkIsNil:userId]) {
        return nil;
    }
    return userId;
}
+ (NSString *)getRecommendGroup:(NSDictionary *)dict
{
    NSDictionary *dic = [dict valueForKey:@"__context"];
    NSDictionary *createDic = dic[@"createdBy"];
    NSString *groupStr = [createDic[@"bodyType"] stringValue];
    
    //NSLog(@"group = %@",groupStr);
    if ([QSCommonUtil checkIsNil:groupStr]) {
        return nil;
    }
    return groupStr;
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

+ (NSArray*)getItems:(NSDictionary *)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    NSArray* itemArray = showDict[@"itemRefs"];
    NSMutableArray* array = [@[] mutableCopy];
    for (id item in itemArray) {
        if ([QSCommonUtil checkIsDict:item]) {
            [array addObject:item];
        }
    }
    return array;
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

+ (NSDictionary*)getPeopleFromShow:(NSDictionary*)showDict
{
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
    NSDictionary* peopleDict = [showDict valueForKeyPath:@"__context.createdBy"];
    if ([QSCommonUtil checkIsDict:peopleDict]) {
        return peopleDict;
    } else {
        return nil;
    }

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


+ (NSString*)getShowDesc:(NSDictionary*)showDict
{
    if (![QSCommonUtil checkIsDict:showDict]) {
        return nil;
    }
    NSString* rec = showDict[@"description"];
    if ([QSCommonUtil checkIsNil:rec]) {
        return nil;
    }
    return rec;
}

+ (NSDate*)getCreatedDate:(NSDictionary*)showDict {
    NSString* dateStr = [showDict valueForKeyPath:@"create"];
    if (!dateStr) {
        return nil;
    }
    NSDate* date = [QSDateUtil buildDateFromResponseString:dateStr];
    return date;
}

+ (BOOL)getSharedByCurrentUser:(NSDictionary*)showDict
{
    NSNumber* like = [showDict valueForKeyPath:@"__context.sharedByCurrentUser"];
    return like.boolValue;
}

+ (NSDictionary*)getPromotionRef:(NSDictionary*)showDict
{
    NSDictionary* dict = [showDict valueForKey:@"__context.promotionRef"];
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    } else {
        return dict;
    }
}
+ (NSString*)getVideoPath:(NSDictionary*)showDict {
    if ([QSCommonUtil checkIsNil:showDict]) {
        return nil;
    }
   // NSLog(@"show Dic Key %@",[showDict allKeys]);
    if ([[showDict allKeys] containsObject:@"video"] ) {
        NSString* videoPath = [showDict valueForKey:@"video"];
        if ([QSCommonUtil checkIsNil:videoPath] || !videoPath.length) {
            return nil;
        }
        return videoPath;
    }
    return nil;
    
}
@end
