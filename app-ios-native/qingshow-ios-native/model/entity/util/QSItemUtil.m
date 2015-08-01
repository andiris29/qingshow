//
//  QSItemUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemUtil.h"
#import "QSEntityUtil.h"
#import "NSNumber+QSExtension.h"
#import <CoreText/CoreText.h>
#import <CoreFoundation/CoreFoundation.h>
#import "QSDateUtil.h"
#import "QSCategoryUtil.h"
#import "QSCategoryManager.h"
#import "NSDictionary+QSExtension.h"
@implementation QSItemUtil
+ (NSArray*)getImagesUrl:(NSDictionary*)itemDict
{
    NSArray* array = itemDict[@"images"];
    if ([QSEntityUtil checkIsNil:array]) {
        return nil;
    }
    NSMutableArray* m = [@[] mutableCopy];
    for (NSDictionary* d in array) {
        NSString* s = d[@"url"];
        NSURL* url = [NSURL URLWithString:s];
        if (url && ![[NSNull null] isEqual:url]) {
            [m addObject:url];
        }
    }
    
    return m;
}

+ (NSURL*)getFirstImagesUrl:(NSDictionary*)itemDict
{
    NSArray* imageUrls = [self getImagesUrl:itemDict];
    if (imageUrls && imageUrls.count ) {
        return imageUrls[0];
    } else {
        return nil;
    }
}

+ (NSString*)getImageDesc:(NSDictionary*)itemDict atIndex:(int)index
{
    NSArray* array = itemDict[@"images"];
    if (index < array.count) {
        NSDictionary* d = array[index];
        return d[@"description"];
    }
    return @"";
}

+ (NSURL*)getShopUrl:(NSDictionary*)itemDict
{
    if (![QSEntityUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSString* path = itemDict[@"source"];
    if (path) {
        NSURL* url = [NSURL URLWithString:path];
        return url;
    }
    return nil;
}

+ (NSString*)getSource:(NSDictionary*)itemDict{
    if (![QSEntityUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSString* path = itemDict[@"source"];
    if (![QSEntityUtil checkIsNil:path]) {
        return path;
    }
    return nil;
}

+ (NSString*)getItemName:(NSDictionary*)itemDict
{
    return [itemDict stringValueForKeyPath:@"name"];
}
+ (NSString*)getItemTypeName:(NSDictionary*)itemDict
{
    if (![QSEntityUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSArray* array = @[@"上衣", @"下装", @"连衣裙", @"内搭", @"鞋子", @"包", @"配饰"];
    QSItemCategory category = [self getItemCategory:itemDict];
    if (category == QSItemCategoryUnknown) {
        return @"";
    } else {
        return array[category];
    }
}

+ (QSItemCategory)getItemCategory:(NSDictionary*)itemDict {

    NSDictionary* categoryDict = [self getCategoryRef:itemDict];
    if (!categoryDict) {
        NSString* categoryId = [self getCategoryStr:itemDict];
        categoryDict = [[QSCategoryManager getInstance] findCategoryOfId:categoryId];
    }
    
    while (categoryDict) {
        NSNumber* com = [QSCategoryUtil getMeasureComposition:categoryDict];
        if (com) {
            return com.intValue;
        } else {
            NSString* parentId = [QSCategoryUtil getParentId:categoryDict];
            categoryDict = [[QSCategoryManager getInstance] findCategoryOfId:parentId];
        }
    }

    
    return QSItemCategoryUnknown;
}

+ (NSNumber*)getMinExpectionPrice:(NSDictionary*)itemDict {
    return [itemDict numberValueForKeyPath:@"minExpectedPrice"];
}

+ (NSNumber*)getPrice:(NSDictionary*)itemDict {
    return [itemDict numberValueForKeyPath:@"price"];
}
+ (NSString*)getPriceDesc:(NSDictionary*)itemDict
{
    return [NSString stringWithFormat:@"%.2f", [self getPrice:itemDict].doubleValue];
}
+ (NSNumber*)getPromoPrice:(NSDictionary*)itemDict {
    return [itemDict numberValueForKeyPath:@"promoPrice"];
}
+ (NSString*)getPromoPriceDesc:(NSDictionary*)itemDict
{
    return [NSString stringWithFormat:@"%.2f", [self getPromoPrice:itemDict].doubleValue];
}

+ (NSURL*)getThumbnail:(NSDictionary *)itemDict {
    NSString* s = [QSEntityUtil getStringValue:itemDict keyPath:@"thumbnail"];
    if (s) {
        return [NSURL URLWithString:s];
    } else {
        return nil;
    }
}

+ (NSDictionary*)getCategoryRef:(NSDictionary*)itemDict {
    return [QSEntityUtil getDictValue:itemDict keyPath:@"categoryRef"];
}

+ (NSString*)getCategoryStr:(NSDictionary*)itemDict {
    //用来处理没有populate的情况
    NSString* str = [QSEntityUtil getStringValue:itemDict keyPath:@"categoryRef"];
    if (!str) {
        NSDictionary* dict = [self getCategoryRef:itemDict];
        str = [QSEntityUtil getIdOrEmptyStr:dict];
    }
    return str;
}

+ (NSArray*)getSkuProperties:(NSDictionary*)itemDict {
    return [itemDict arrayValueForKeyPath:@"skuProperties"];
}
@end
