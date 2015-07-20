//
//  QSItemUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemUtil.h"
#import "QSCommonUtil.h"
#import "NSNumber+QSExtension.h"
#import <CoreText/CoreText.h>
#import <CoreFoundation/CoreFoundation.h>
#import "QSDateUtil.h"
#import "QSCategoryUtil.h"
#import "QSTaobaoInfoUtil.h"
#import "QSCategoryManager.h"
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

+ (NSString*)getSource:(NSDictionary*)itemDict{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSString* path = itemDict[@"source"];
    if (![QSCommonUtil checkIsNil:path]) {
        return path;
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
            return [NSString stringWithFormat:@"￥%.2f", (minPrice.doubleValue)];
        } else {
            return [NSString stringWithFormat:@"￥%.2f-%.2f", minPrice.doubleValue, maxPrice.doubleValue];
        }
    } else {
        //min(skus[i].price) - 0.01
        return [NSString stringWithFormat:@"￥%.2f", (minPrice.doubleValue)];
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
    return [NSString stringWithFormat:@"￥%.2f", (minPrice.doubleValue)];
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


+ (BOOL)getIsLike:(NSDictionary*)itemDict
{
    if ([QSCommonUtil checkIsNil:itemDict]) {
        return NO;
    }
    NSDictionary* context = itemDict[@"__context"];
    if (context) {
        return ((NSNumber*)context[@"likedByCurrentUser"]).boolValue;
    }
    return NO;
}

+ (void)setIsLike:(BOOL)isLike item:(NSDictionary*)itemDict
{
    if ([QSCommonUtil checkIsNil:itemDict]) {
        return;
    }
    if ([itemDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)itemDict;
        NSDictionary* context = itemDict[@"__context"];
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

+ (void)addNumberLike:(long long)num forItem:(NSDictionary*)itemDict
{
    if ([QSCommonUtil checkIsNil:itemDict]) {
        return;
    }
    if ([itemDict isKindOfClass:[NSMutableDictionary class]]) {
        NSMutableDictionary* s = (NSMutableDictionary*)itemDict;
        long long preNumlike = ((NSNumber*)s[@"numLike"]).longLongValue;
        s[@"numLike"] = @(preNumlike + num);
    }
}

+ (NSString*)getNumberLikeDescription:(NSDictionary*)itemDict
{
    if ([QSCommonUtil checkIsNil:itemDict]) {
        return nil;
    }
    return ((NSNumber*)itemDict[@"numLike"]).kmbtStringValue;
}

+ (NSDate*)getLikeDate:(NSDictionary*)itemDict
{
    NSString* dateStr = [itemDict valueForKeyPath:@"__context.likeDate"];
    if (!dateStr) {
        return nil;
    }
    NSDate* date = [QSDateUtil buildDateFromResponseString:dateStr];
    return date;
}

+ (NSString*)getSelectedSku:(NSDictionary*)item
{
    NSString* source = [self getSource:item];
    if ([QSCommonUtil checkIsNil:source]) {
        return nil;
    }
    NSArray* comps = [source componentsSeparatedByString:@"&"];
    NSString* skuStr = nil;
    for (NSString* c in comps) {
        if ([c hasPrefix:@"sku="]) {
            NSArray* a = [c componentsSeparatedByString:@"="];
            skuStr = [a lastObject];
        }
    }
    if (!skuStr) {
        NSArray* array = [self getSkusArray:item];
        if (array.count) {
            NSDictionary* skuDict = [array firstObject];
            skuStr = skuDict[@"sku_id"];
        }
    }
    skuStr = [NSString stringWithFormat:@"%@", skuStr];
    return skuStr;
}

+ (NSString*)getItemColorDesc:(NSDictionary*)item
{
    NSString* sku = [self getSelectedSku:item];
    NSDictionary* taobaoInfo = [self getTaobaoInfo:item];
    
    return [QSTaobaoInfoUtil getColorPropertyName:taobaoInfo sku:sku];
    
}
+ (NSURL*)getThumbnail:(NSDictionary *)itemDict {
    NSString* s = [QSCommonUtil getStringValue:itemDict key:@"thumbnail"];
    if (s) {
        return [NSURL URLWithString:s];
    } else {
        return nil;
    }
}

+ (NSDictionary*)getCategoryRef:(NSDictionary*)itemDict {
    return [QSCommonUtil getDictValue:itemDict key:@"categoryRef"];
}

+ (NSString*)getCategoryStr:(NSDictionary*)itemDict {
    //用来处理没有populate的情况
    return [QSCommonUtil getStringValue:itemDict key:@"categoryRef"];
}
@end
