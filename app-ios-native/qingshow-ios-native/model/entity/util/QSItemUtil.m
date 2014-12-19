//
//  QSItemUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemUtil.h"
#import "QSCommonUtil.h"
#import <CoreText/CoreText.h>
#import <CoreFoundation/CoreFoundation.h>
@implementation QSItemUtil
+ (NSURL*)getCoverUrl:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSString* path = itemDict[@"cover"];
    if (path) {
        NSURL* url = [NSURL URLWithString:path];
        return url;
    }
    return nil;
}
+ (NSURL*)getShopUrl:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSString* path = itemDict[@"source"];
    if (path) {
        NSURL* url = [NSURL URLWithString:path];
        return url;
    }
    return nil;
}
+ (NSAttributedString*)getItemsAttributedDescription:(NSArray*)itemsArray
{
    if ([QSCommonUtil checkIsNil:itemsArray]) {
        return nil;
    }
    if (!itemsArray.count || ![itemsArray[0] isKindOfClass:[NSDictionary class]]) {
        return [[NSAttributedString alloc] init];
    }
    
    NSMutableAttributedString* str = [[NSMutableAttributedString alloc] init];
    for (NSDictionary* itemDict in itemsArray) {
        NSString* typeStr = [QSItemUtil getItemTypeName:itemDict];
//        NSAttributedString* typeAttributedStr = [NSAttributedString alloc] initWithString:typeStr attributes:@{}
        NSString* des = [QSItemUtil getItemDescription:itemDict];
        NSMutableAttributedString * a = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"%@ %@ ", typeStr, des] attributes:nil];

        [a addAttribute:NSFontAttributeName value:CFBridgingRelease(CTFontCreateWithName((CFStringRef)[UIFont fontWithName:@"Arial" size:14].fontName, 14, nil)) range:NSMakeRange(0, a.length)];
        [a addAttribute:NSForegroundColorAttributeName value:[UIColor colorWithRed:78.f/255.f green:78.f/255.f blue:78.f/255.f alpha:1.f] range:NSMakeRange(0, a.length)];
        [a addAttribute:NSForegroundColorAttributeName
                  value:[UIColor colorWithRed:238.f/255.f green:120.f/255.f blue:37.f/255.f alpha:1.f]
                  range:NSMakeRange(0, typeStr.length)];

        [str appendAttributedString:a];
    }
    return str;
}
+ (NSString*)getItemDescription:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    return itemDict[@"name"];
}
+ (NSString*)getItemTypeName:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSArray* array = @[@"上装", @"下装", @"鞋子", @"配饰"];
    NSNumber* category = itemDict[@"category"];
    if (category.intValue < array.count) {
        return array[category.intValue];
    } else {
        return @"";
    }
}

+ (NSDictionary*)getBrand:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    return itemDict[@"brandRef"];
}
+ (NSArray*)getItemsImageUrlArray:(NSArray*)itemArray;
{
    if ([QSCommonUtil checkIsNil:itemArray]) {
        return nil;
    }
    if (!itemArray.count || ![itemArray[0] isKindOfClass:[NSDictionary class]]) {
        return @[];
    }
    
    NSMutableArray* array = [@[] mutableCopy];
    for (NSDictionary* itemDict in itemArray) {
        NSString* path = itemDict[@"cover"];
        NSURL* url = [NSURL URLWithString:path];
        [array addObject:url];
    }
    return array;
}
+ (NSString*)getPrice:(NSDictionary*)item
{
#warning 没字段
    return @"";
}
+ (NSURL*)getIconUrl:(NSDictionary*)itemDict
{
#warning 没字段
    return nil;
}
@end
