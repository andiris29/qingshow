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

+ (NSURL*)getShopUrl:(NSDictionary*)itemDict
{
    if (![QSEntityUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSString* path = itemDict[@"source"];
    if (path) {
        NSURL* url = [NSURL URLWithString:path];
        if (!url) {
            NSString* newPath = [path stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            url = [NSURL URLWithString:newPath];
        }
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
