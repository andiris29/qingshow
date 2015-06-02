//
//  QSTaobaoInfoUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTaobaoInfoUtil.h"
#import "QSCommonUtil.h"

#define COLOR_PRE @"1627207"

@interface QSTaobaoInfoUtil ()
+ (NSDictionary*)getFirstSku:(NSDictionary*)taobaoInfo;

@end

@implementation QSTaobaoInfoUtil

#pragma mark - Private
+ (NSArray*)getAllPropertyComponent:(NSDictionary*)taobaoInfo
{
    NSArray* skus = [self getSkusArray:taobaoInfo];
    NSMutableArray* retArray = [@[] mutableCopy];
    [skus enumerateObjectsUsingBlock:^(NSDictionary* sku, NSUInteger idx, BOOL *stop) {
        NSString* properties = sku[@"properties"];
        NSArray* propertyComp = [properties componentsSeparatedByString:@";"];
        [propertyComp enumerateObjectsUsingBlock:^(id obj2, NSUInteger idx2, BOOL *sto2) {
            NSString* property = (NSString*)obj2;
            if (property.length) {
                if ([retArray indexOfObject:property] == NSNotFound) {
                    [retArray addObject:property];
                }
            }
        }];
    }];
    return retArray;
}

+ (NSArray*)getSkusArray:(NSDictionary*)taobaoInfo
{
    if (![QSCommonUtil checkIsDict:taobaoInfo]) {
        return nil;
    }
    
    NSArray* skus = taobaoInfo[@"skus"];
    if (![QSCommonUtil checkIsArray:skus]) {
        return nil;
    } else {
        return skus;
    }
}


+ (NSDictionary*)getFirstSku:(NSDictionary*)taobaoInfo
{
    NSArray* skus = [self getSkusArray:taobaoInfo];
    if (!skus || !skus.count) {
        return nil;
    }
    NSDictionary* sku = skus[0];
    if ([QSCommonUtil checkIsDict:sku]) {
        return sku;
    } else {
        return nil;
    }
}

+ (NSDictionary*)findSkusWithSkuId:(NSString*)skuId taobaoInfo:(NSDictionary*)taobaoInfo
{
    if ([skuId respondsToSelector:@selector(stringValue)]) {
#warning workaround class of skuId
        skuId = ((NSNumber*)skuId).stringValue;
    }
    
    NSArray* skus = [self getSkusArray:taobaoInfo];
    for (NSDictionary* sku in skus) {
        NSString* rSkuId = sku[@"sku_id"];
        if ([rSkuId respondsToSelector:@selector(stringValue)]) {
            rSkuId =  ((NSNumber*)rSkuId).stringValue;
        }
        if ([skuId isEqualToString:rSkuId]) {
            return sku;
        }
    }
    return nil;
}


#pragma mark - Public;
+ (NSString*)getNameOfProperty:(NSString*)property taobaoInfo:(NSDictionary *)taobaoInfo
{
    NSArray* skus = [self getSkusArray:taobaoInfo];
    for (NSDictionary* sku in skus) {
        NSString* properties = sku[@"properties"];
        if ([properties rangeOfString:property].location != NSNotFound) {
            NSString* propName = sku[@"properties_name"];
            if (![QSCommonUtil checkIsNil:propName] && propName.length) {
                NSArray* pArray = [properties componentsSeparatedByString:@";"];
                NSMutableArray* filterPropArray = [@[] mutableCopy];
                [pArray enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
                    if (((NSString*)obj).length) {
                        [filterPropArray addObject:obj];
                    }
                }];
                
                NSArray* pNameArray = [propName componentsSeparatedByString:@";"];
                NSMutableArray* filterPropNameArray = [@[] mutableCopy];
                [pNameArray enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
                    if (((NSString*)obj).length) {
                        [filterPropNameArray addObject:obj];
                    }
                }];
                
                NSUInteger index = [filterPropArray indexOfObject:property];
                return filterPropNameArray[index];
            } else {
                return nil;
            }
        }
    }
    return nil;
}

+ (NSNumber*)getPriceOfSkuId:(NSString*)skuId taobaoInfo:(NSDictionary*)taobaoInfo quantity:(NSNumber*)quantity
{
    NSDictionary* sku = [self findSkusWithSkuId:skuId taobaoInfo:taobaoInfo];
    return sku[@"price"];
}
+ (NSNumber*)getPromoPriceOfSkuId:(NSString*)skuId taobaoInfo:(NSDictionary*)taobaoInfo quantity:(NSNumber*)quantity
{
    NSDictionary* sku = [self findSkusWithSkuId:skuId taobaoInfo:taobaoInfo];
    return sku[@"promo_price"];
}

+ (NSString*)getColorPropertyId:(NSDictionary*)taobaoInfoDict sku:(NSString*)skuId {
    if (![QSCommonUtil checkIsDict:taobaoInfoDict]) {
        return nil;
    }
    NSDictionary* skuDict = [self findSkusWithSkuId:skuId taobaoInfo:taobaoInfoDict];
    NSString* propertiesName = skuDict[@"properties"];
    NSArray* array = [propertiesName componentsSeparatedByString:@";"];
    NSString* colorProp = nil;
    for (NSString* str in array) {
        if ([str hasPrefix:COLOR_PRE]) {
            colorProp = str;
        }
    }
    return colorProp;
}
+ (NSString*)getColorPropertyName:(NSDictionary*)taobaoInfo sku:(NSString*)skuId {
    NSString* colorPropertyId = [self getColorPropertyId:taobaoInfo sku:skuId];
    if (!colorPropertyId) {
        return nil;
    }
    
    return [self getNameOfProperty:colorPropertyId taobaoInfo:taobaoInfo];
}

@end
