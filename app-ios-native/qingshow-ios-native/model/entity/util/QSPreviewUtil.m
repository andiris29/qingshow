//
//  QSPreviewUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/28/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSPreviewUtil.h"
#import "QSCommonUtil.h"
#import "NSNumber+QSExtension.h"
#import "QSDateUtil.h"

@implementation QSPreviewUtil

+ (NSURL*)getCoverUrl:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return nil;
    }
    NSString* path = previewDict[@"cover"];
    return [NSURL URLWithString:path];
}
+ (NSDictionary*)getCoverMetadata:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return nil;
    }
    return previewDict[@"coverMetadata"];
}
+ (NSString*)getNumLikeDesc:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return nil;
    }
    return ((NSNumber*)previewDict[@"numLike"]).kmbtStringValue;
}
+ (NSString*)getBrandDesc:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return nil;
    }
    return previewDict[@"brandDescription"];
}
+ (NSString*)getNameDesc:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return nil;
    }
    return previewDict[@"nameDescription"];
}
+ (NSString*)getPriceDesc:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return nil;
    }
    return previewDict[@"priceDescription"];
}
+ (NSString*)getCreateDesc:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return nil;
    }
    NSString* dateStr = previewDict[@"create"];
    NSDate* d = [QSDateUtil buildDateFromResponseString:dateStr];
    return [QSDateUtil buildStringFromDate:d];
}

+ (BOOL)getIsLike:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return NO;
    }
    NSDictionary* context = previewDict[@"__context"];
    if (context) {
        return ((NSNumber*)context[@"likedByCurrentUser"]).boolValue;
    }
    return NO;
}
+ (void)setIsLike:(BOOL)isLike preview:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return;
    }
    if ([previewDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)previewDict;
        NSDictionary* context = previewDict[@"__context"];
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
+ (void)addNumberLike:(long long)num forShow:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return;
    }
    if ([previewDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)previewDict;
        long long preNumlike = ((NSNumber*)s[@"numLike"]).longLongValue;
        s[@"numLike"] = @(preNumlike + num);
    }
}

+ (NSString*)getNumCommentDesc:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict]) {
        return @"0";
    }
    NSDictionary* context = previewDict[@"__context"];
    if (context) {
        return ((NSNumber*)context[@"numComments"]).kmbtStringValue;
    }
    return @"0";
}
+ (void)addNumberComment:(long long)num forPreview:(NSDictionary*)previewDict
{
    if ([QSCommonUtil checkIsNil:previewDict] || ![previewDict isKindOfClass:[NSMutableDictionary class]]) {
        return;
    }
    NSMutableDictionary* m = (NSMutableDictionary*)previewDict;
    NSMutableDictionary* context = [previewDict[@"__context"] mutableCopy];
    if (context) {
        if ([QSCommonUtil checkIsNil:context[@"numComments"]]) {
            return;
        }
        context[@"numComments"] = @(((NSNumber*)context[@"numComments"]).longLongValue + num);
        m[@"__context"] = context;
    }
}
@end
