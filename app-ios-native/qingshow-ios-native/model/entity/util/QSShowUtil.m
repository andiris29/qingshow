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
#import "QSEntityUtil.h"
#import "NSArray+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"
#import "QSDateUtil.h"
#import "QSNetworkKit.h"
@implementation QSShowUtil
+ (NSURL*)getHoriCoverUrl:(NSDictionary*)dict
{
    if ([QSEntityUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = dict[@"horizontalCover"];
    if ([QSEntityUtil checkIsNil:cover]) {
        return [self getCoverUrl:dict];
    } else {
        return [NSURL URLWithString:cover];
    }
}
//新增获取高度的实现代码
+ (CGFloat)getCoverMetaDataHeight:(NSDictionary *)dic
{
    if ([QSEntityUtil checkIsNil:dic]) {
        return 180;
    }
    if ([QSEntityUtil checkIsNil:dic[@"coverMetaData.height"]]) {
        return 180;
    }
    else{
        return  [dic[@"coverMetaData.height"] floatValue];
    }
}
+ (NSURL*)getCoverUrl:(NSDictionary*)dict
{
    if ([QSEntityUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = dict[@"cover"];
    if ([QSEntityUtil checkIsNil:cover]) {
        return nil;
    }
    return [NSURL URLWithString:cover];
    
}

+ (NSString *)getShowId:(NSDictionary *)dict
{
    return [QSEntityUtil getStringValue:dict keyPath:@"_id"];
}
+ (NSURL *)getFormatterCoverUrl:(NSDictionary *)dict
{
    if ([QSEntityUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = dict[@"cover"];
   
    NSRange range = [cover rangeOfString:@"cover/"];
    NSString *rangeStr = [cover substringFromIndex:range.location];
    NSString *formatterStr = [NSString stringWithFormat:@"http://trial01.focosee.com/img/show/%@_s.jpeg",rangeStr];
    if (cover) {
        return [NSURL URLWithString:formatterStr];
    }
    else {
        return nil;
    }
}
+ (NSURL *)getFormatterCoVerForegroundUrl:(NSDictionary *)dict
{
    if ([QSEntityUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* coverFore = dict[@"coverForeground"];
    NSRange range = [coverFore rangeOfString:@".png"];
    NSString *rangeStr = [coverFore substringToIndex:range.location];
    NSString *formatterStr = [NSString stringWithFormat:@"%@_s.png",rangeStr];
    if (coverFore) {
        return [NSURL URLWithString:formatterStr];
    }
    else {
        return nil;
    }
 
}


+ (NSURL*)getCoverBackgroundUrl:(NSDictionary*)dict {
    if ([QSEntityUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = [dict valueForKeyPath:@"coverInfo.background"];
    if ([QSEntityUtil checkIsNil:cover]) {
        return nil;
    }
    return [NSURL URLWithString:cover];
}

+ (NSURL*)getCoverForegroundUrl:(NSDictionary*)dict {
    if ([QSEntityUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* cover = [dict valueForKey:@"coverForeground"];
    
    if ([QSEntityUtil checkIsNil:cover]) {
        return nil;
    }
    return [NSURL URLWithString:cover];
}

+ (NSString *)getUserId:(NSDictionary *)dict
{
    NSString  *userId  = dict[@"id"];
    if ([QSEntityUtil checkIsNil:userId]) {
        return nil;
    }
    return userId;
}
+ (NSString *)getRecommendGroup:(NSDictionary *)dict
{
    NSDictionary *dic = [dict valueForKey:@"__context"];
    NSDictionary *createDic = dic[@"createdBy"];
    NSString *groupStr = [createDic[@"bodyType"] stringValue];

    if ([QSEntityUtil checkIsNil:groupStr]) {
        return nil;
    }
    return groupStr;
}

+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict
{
    if ([QSEntityUtil checkIsNil:dict]) {
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
    if ([QSEntityUtil checkIsNil:showDict]) {
        return nil;
    }
    NSArray* itemArray = showDict[@"itemRefs"];
    NSMutableArray* returnArray = [@[] mutableCopy];
    for (id item in itemArray) {
        //Ignore dict that hasn't been populated
        if ([QSEntityUtil checkIsDict:item]) {
            [returnArray addObject:item];
        }
    }
    return returnArray;
}
+ (NSArray*)getAllItemArray:(NSDictionary*)showDict {
    return [showDict arrayValueForKeyPath:@"itemRefs"];
}

+ (NSArray*)getItemRects:(NSDictionary*)showDict {
    return [showDict arrayValueForKeyPath:@"itemRects"];
}
+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index
{
    if ([QSEntityUtil checkIsNil:showDict]) {
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
    if ([QSEntityUtil checkIsNil:showDict]) {
        return nil;
    }
    NSDictionary* peopleDict = [showDict valueForKeyPath:@"ownerRef"];
    if ([QSEntityUtil checkIsDict:peopleDict]) {
        return peopleDict;
    } else {
        return nil;
    }

}

+ (NSString*)getNumberCommentsDescription:(NSDictionary*)showDict
{
    if ([QSEntityUtil checkIsNil:showDict]) {
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
    if ([QSEntityUtil checkIsNil:showDict] || ![showDict isKindOfClass:[NSMutableDictionary class]]) {
        return;
    }
    NSMutableDictionary* m = (NSMutableDictionary*)showDict;
    NSMutableDictionary* context = [showDict[@"__context"] mutableCopy];
    if (context) {
        if ([QSEntityUtil checkIsNil:context[@"numComments"]]) {
            return;
        }
        context[@"numComments"] = @(((NSNumber*)context[@"numComments"]).longLongValue + num);
        m[@"__context"] = context;
    }
}

+ (NSString*)getNumberLikeDescription:(NSDictionary*)showDict
{
    if ([QSEntityUtil checkIsNil:showDict]) {
        return nil;
    }
    return ((NSNumber*)showDict[@"numLike"]).kmbtStringValue;
}

+ (NSString*)getNumberViewDesc:(NSDictionary*)showDict {
    NSNumber* n = [showDict numberValueForKeyPath:@"numView"];
    n = n ? n : @0;
    return n.kmbtStringValue;
}

+ (NSString*)getNumberItemDescription:(NSDictionary*)showDict
{
    if ([QSEntityUtil checkIsNil:showDict]) {
        return nil;
    }
    return @([self getItems:showDict].count).kmbtStringValue;
}

+ (BOOL)getIsLike:(NSDictionary*)showDict
{
    if ([QSEntityUtil checkIsNil:showDict]) {
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
    if ([QSEntityUtil checkIsNil:showDict]) {
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
    if ([QSEntityUtil checkIsNil:showDict]) {
        return;
    }
    if ([showDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)showDict;
        long long preNumlike = ((NSNumber*)s[@"numLike"]).longLongValue;
        preNumlike += num;
        s[@"numLike"] = @(preNumlike);
    }
}

+ (void)addNumberView:(long long)num forShow:(NSDictionary*)showDict {
#warning Refactor
    if ([QSEntityUtil checkIsNil:showDict]) {
        return;
    }
    if ([showDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)showDict;
        long long preNumlike = ((NSNumber*)s[@"numView"]).longLongValue;
        preNumlike += num;
        preNumlike = preNumlike >= 0 ? preNumlike : 0;
        s[@"numView"] = @(preNumlike);
    }
}

+ (NSDate*)getRecommendDate:(NSDictionary*)showDict
{
    if (![QSEntityUtil checkIsDict:showDict]) {
        return nil;
    }
    NSDictionary* rec = showDict[@"recommend"];
    if (![QSEntityUtil checkIsDict:rec]) {
        return nil;
    }
    id date = rec[@"date"];
    
    if ([QSEntityUtil checkIsNil:date]) {
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
    if (![QSEntityUtil checkIsDict:showDict]) {
        return nil;
    }
    NSDictionary* rec = showDict[@"recommend"];
    if (![QSEntityUtil checkIsDict:rec]) {
        return nil;
    }
    return rec[@"description"];
}


+ (NSString*)getShowDesc:(NSDictionary*)showDict
{
    if (![QSEntityUtil checkIsDict:showDict]) {
        return nil;
    }
    NSString* rec = showDict[@"description"];
    if ([QSEntityUtil checkIsNil:rec]) {
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
    if ([QSEntityUtil checkIsNil:dict]) {
        return nil;
    } else {
        return dict;
    }
}
+ (NSString*)getVideoPath:(NSDictionary*)showDict {
    if ([QSEntityUtil checkIsNil:showDict]) {
        return nil;
    }
    if ([[showDict allKeys] containsObject:@"video"] ) {
        NSString* videoPath = [showDict valueForKey:@"video"];
        if ([QSEntityUtil checkIsNil:videoPath] || !videoPath.length) {
            return nil;
        }
        return videoPath;
    }
    return nil;
    
}

+ (BOOL)getItemReductionEnabled:(NSDictionary*)showDict {
    NSNumber* n = [showDict numberValueForKeyPath:@"itemReductionEnabled"];
    if (n) {
        return n.boolValue;
    } else {
        return YES;
    }
}
@end
