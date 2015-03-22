//
//  QSTaobaoInfoUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTaobaoInfoUtil.h"
#import "QSCommonUtil.h"

#define SIZE_PRE @"205"
#define COLOR_PRE @"162"

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
        
        if ([rSkuId isEqual:skuId]) {
            return sku;
        }
    }
    return nil;
}

+ (NSString*)getSizeOfSku:(NSDictionary*)skuDict
{
    NSArray* propComponents = [skuDict[@"properties"] componentsSeparatedByString:@";"];
    NSMutableArray* filterProp = [@[] mutableCopy];
    for (NSString* comp in propComponents) {
        if (comp.length) {
            [filterProp addObject:comp];
        }
    }
    
    NSArray* propNames = [skuDict[@"properties_name"] componentsSeparatedByString:@";"];
    
    for (int i = 0; i < filterProp.count; i++) {
        NSString* comp = filterProp[i];
        if ([comp hasPrefix:SIZE_PRE]) {
            if (i < propNames.count) {
                return propNames[i];
            } else {
                return @"";
            }
        }
    }
    
    return @"";
}
+ (NSString*)getColorOfSku:(NSDictionary*)skuDict
{
    NSArray* propComponents = [skuDict[@"properties"] componentsSeparatedByString:@";"];
    NSMutableArray* filterProp = [@[] mutableCopy];
    for (NSString* comp in propComponents) {
        if (comp.length) {
            [filterProp addObject:comp];
        }
    }
    
    NSArray* propNames = [skuDict[@"properties_name"] componentsSeparatedByString:@";"];
    
    for (int i = 0; i < filterProp.count; i++) {
        NSString* comp = filterProp[i];
        if ([comp hasPrefix:COLOR_PRE]) {
            if (i < propNames.count) {
                return propNames[i];
            } else {
                return @"";
            }
        }
    }
    
    return @"";
}

+ (NSDictionary*)findSkusOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary*)taobaoInfo
{
    NSMutableArray* array = [@[] mutableCopy];
    if (sizeSku) {
        [array addObject:sizeSku];
    }
    if (colorSku) {
        [array addObject:colorSku];
    }
    if (!array.count) {
        return nil;
    }
    
    NSArray* skus = [self getSkusArray:taobaoInfo];
    for (NSDictionary* sku in skus) {
        NSString* property = sku[@"properties"];
        NSArray* comps = [property componentsSeparatedByString:@";"];
        BOOL f = YES;
        for (NSString* c in comps) {
            if (c.length) {
                if ([array indexOfObject:c] == NSNotFound) {
                    f = NO;
                    break;
                }
            }
        }
        if (f) {
            return sku;
        }
        
    }
    return nil;
}

#pragma mark - Public;
+ (BOOL)hasSizeSku:(NSDictionary*)taobaoInfo
{
    return [self getSizeSkus:taobaoInfo].count != 0;
}
+ (BOOL)hasColorSku:(NSDictionary*)taobaoInfo
{
    return [self getColorSkus:taobaoInfo].count != 0;
}

+ (BOOL)hasPropertiesThumbnail:(NSDictionary*)taobaoInfo
{
    NSDictionary* sku = [self getFirstSku:taobaoInfo];
    NSString* properties = sku[@"properties_thumbnail"];
    if ([QSCommonUtil checkIsNil:properties]) {
        return NO;
    } else {
        return YES;
    }
}

+ (NSArray*)getSizeSkus:(NSDictionary*)taobaoInfo
{
    NSMutableArray* retArray = [@[] mutableCopy];
    NSArray* components = [self getAllPropertyComponent:taobaoInfo];
    [components enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSString* c = (NSString*)obj;
        if ([c hasPrefix:SIZE_PRE]) {
            [retArray addObject:c];
        }
    }];
    return retArray;
}

+ (NSArray*)getColorSkus:(NSDictionary*)taobaoInfo
{
    NSMutableArray* retArray = [@[] mutableCopy];
    NSArray* components = [self getAllPropertyComponent:taobaoInfo];
    [components enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSString* c = (NSString*)obj;
        if ([c hasPrefix:COLOR_PRE]) {
            [retArray addObject:c];
        }
    }];
    return retArray;
}
+ (NSURL*)getThumbnailUrlOfProperty:(NSString*)property taobaoInfo:(NSDictionary*)taobaoInfo;
{
    NSArray* skus = [self getSkusArray:taobaoInfo];
    for (NSDictionary* sku in skus) {
        NSString* properties = sku[@"properties"];
        if ([properties rangeOfString:property].location != NSNotFound) {
            NSString* urlStr = sku[@"properties_thumbnail"];
            if (![QSCommonUtil checkIsNil:urlStr] && urlStr.length) {
                return [NSURL URLWithString:urlStr];
            } else {
                return nil;
            }
        }
    }
    return nil;
}


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

+ (NSString*)getPriceOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary *)taobaoInfo
{
    NSDictionary* sku = [self findSkusOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    if (!sku) {
        return nil;
    }
    NSNumber* p = sku[@"price"];
    return [NSString stringWithFormat:@"￥%.2f",p.doubleValue];
}

+ (NSString*)getPromoPriceOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary *)taobaoInfo quanitty:(NSNumber*)quantity
{
    NSDictionary* sku = [self findSkusOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    if (!sku) {
        return nil;
    }
    NSNumber* p = sku[@"promo_price"];
    
    return [NSString stringWithFormat:@"￥%.2f",(p.doubleValue * quantity.intValue)];
}

+ (NSString*)getPromoPriceOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary *)taobaoInfo
{
    NSDictionary* sku = [self findSkusOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    if (!sku) {
        return nil;
    }
    NSNumber* p = sku[@"promo_price"];
    return [NSString stringWithFormat:@"￥%.2f",p.doubleValue];
}

+ (NSNumber*)getSkuOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary *)taobaoInfo
{
    NSDictionary* sku = [self findSkusOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    if (!sku) {
        return nil;
    }
    NSNumber* p = sku[@"sku_id"];
    return p;
}

+ (BOOL)getIsAvaliableOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary *)taobaoInfo
{
    NSDictionary* sku = [self findSkusOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    NSNumber* stock = sku[@"stock"];
    return stock.longLongValue != 0ll;
}

@end
