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
+ (NSArray*)getImagesUrl:(NSDictionary*)itemDict
{
    NSArray* array = itemDict[@"images"];
    if ([QSCommonUtil checkIsNil:array]) {
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
+ (NSDictionary*)getImageMetadata:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    return itemDict[@"imageMetadata"];
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

+ (NSString*)getItemName:(NSDictionary*)itemDict
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

+ (NSArray*)getSkusArray:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSDictionary* taobaoInfoDict = [self getTaobaoInfo:itemDict];
    if (![QSCommonUtil checkIsDict:taobaoInfoDict]) {
        return nil;
    }
    
    NSArray* skus = taobaoInfoDict[@"skus"];
    if (![QSCommonUtil checkIsArray:skus]) {
        return nil;
    }
    return skus;
}

+ (BOOL)hasDiscountInfo:(NSDictionary*)itemDict
{

    NSArray* skus = [self getSkusArray:itemDict];
    if (!skus || !skus.count) {
        return NO;
    }
    
    //Check at least one dict
    BOOL f = YES;
    for (NSDictionary* sku in skus) {
        if ([QSCommonUtil checkIsDict:sku]) {
            f = NO;
            break;
        }
    }
    if (f) {
        return NO;
    }
    
    //Check at least one promo_price
    f = YES;
    for (NSDictionary* sku in skus) {
        if ([QSCommonUtil checkIsDict:sku]) {
            id promoPrice = sku[@"promo_price"];
            if (![QSCommonUtil checkIsNil:promoPrice]) {
                f = NO;
                break;
            }
        }
    }
    if (f) {
        return NO;
    }
    
    f = NO;
    for (NSDictionary* sku in skus) {
        if ([QSCommonUtil checkIsDict:sku]) {
            NSNumber* promoPrice = sku[@"promo_price"];
            NSNumber* price = sku[@"price"];
            if (ABS(price.doubleValue - promoPrice.doubleValue) > 0.01) {
                f = YES;
                break;
            }
        }
    }
    return f;
}

+ (NSString*)getPrice:(NSDictionary*)itemDict
{
    NSArray* skus = [self getSkusArray:itemDict];
    if (!skus || !skus.count) {
        return @"";
    }
    NSArray* sortedSkus = [skus sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        NSDictionary* d1 = (NSDictionary*)obj1;
        NSDictionary* d2 = (NSDictionary*)obj2;
        NSNumber* price1 = (NSNumber*)d1[@"price"];
        NSNumber* price2 = (NSNumber*)d2[@"price"];
        return price1.doubleValue > price2.doubleValue;
    }];
    NSDictionary* minSku = [sortedSkus firstObject];
    NSNumber* minPrice = minSku[@"price"];
    NSDictionary* maxSku = [sortedSkus lastObject];
    NSNumber* maxPrice = maxSku[@"price"];
    if ([self hasDiscountInfo:itemDict]) {
        if (sortedSkus.count == 1 || (ABS(maxPrice.doubleValue - minPrice.doubleValue)) < 0.01) {
            return [NSString stringWithFormat:@"￥%.2f", (minPrice.doubleValue - 0.01)];
        } else {
            return [NSString stringWithFormat:@"￥%.2f-%.2f", minPrice.doubleValue, maxPrice.doubleValue];
        }
    } else {
        //min(skus[i].price) - 0.01
        return [NSString stringWithFormat:@"￥%.2f", (minPrice.doubleValue - 0.01)];
    }
    /*
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSNumber* price = itemDict[@"price"];
    if ([QSCommonUtil checkIsNil:price]) {
        return @"";
    } else {
        double priceDouble = price.doubleValue;
        return [NSString stringWithFormat:@"￥%.2f", priceDouble];
    }
     */
}
+ (NSString*)getPriceAfterDiscount:(NSDictionary*)itemDict
{
    NSArray* skus = [self getSkusArray:itemDict];
    if (!skus || !skus.count) {
        return @"";
    }
    if (![self hasDiscountInfo:itemDict]) {
        return @"";
    }
    NSArray* sortedSkus = [skus sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        NSDictionary* d1 = (NSDictionary*)obj1;
        NSDictionary* d2 = (NSDictionary*)obj2;
        NSNumber* price1 = (NSNumber*)d1[@"promo_price"];
        NSNumber* price2 = (NSNumber*)d2[@"promo_price"];
        return price1.doubleValue > price2.doubleValue;
    }];
    NSDictionary* minSku = [sortedSkus firstObject];
    NSNumber* minPrice = minSku[@"promo_price"];
    return [NSString stringWithFormat:@"￥%.2f", (minPrice.doubleValue - 0.01)];
    /*
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    //brandDiscountInfo.price
    NSDictionary* brandDiscountInfo = itemDict[@"brandDiscountInfo"];
    if (![QSCommonUtil checkIsDict:brandDiscountInfo]) {
        return nil;
    }
    NSNumber* price = brandDiscountInfo[@"price"];
    if ([QSCommonUtil checkIsNil:price]) {
        return @"";
    } else {
        double priceDouble = price.doubleValue;
        return [NSString stringWithFormat:@"￥%.2f", priceDouble];
    }
     */
}

+ (NSDictionary*)getTaobaoInfo:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    return itemDict[@"taobaoInfo"];
}

+ (NSURL*)getSizeExplanation:(NSDictionary*)item
{
    if (![QSCommonUtil checkIsDict:item]) {
        return nil;
    }
    NSString* e = item[@"sizeExplanation"];
    if (![QSCommonUtil checkIsNil:e]) {
        return [NSURL URLWithString:e];
    }
    return nil;
}

+ (NSString*)getVideoPath:(NSDictionary*)item
{
    if (![QSCommonUtil checkIsDict:item]) {
        return nil;
    }
    NSString* path = item[@"video"];
    if ([QSCommonUtil checkIsNil:path]) {
        return nil;
    } else {
        return path;
    }
}
+ (NSDictionary*)getBrand:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSDictionary* b = itemDict[@"brandRef"];
    if ([QSCommonUtil checkIsNil:b]) {
        return b;
    } else {
        NSMutableDictionary* mb = [b mutableCopy];
        [self setBrand:mb forItem:itemDict];
        return mb;
    }
}
+ (void)setBrand:(NSDictionary*)brandDict forItem:(NSDictionary*)item
{
    if (![item isKindOfClass:[NSMutableDictionary class]]) {
        return;
    }
    NSMutableDictionary* m = (NSMutableDictionary*)item;
    m[@"brandRef"] = brandDict;
}
@end
