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
#import "QSTaobaoInfoUtil.h"
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
#warning just workaround
    NSDictionary* categoryDict = [self getCategoryRef:itemDict];
    NSString* categoryId = @"";
    if (categoryDict) {
        categoryId = [QSCommonUtil getIdOrEmptyStr:categoryDict];
    } else {
        categoryId = itemDict[@"categoryRef"];
    }

    NSArray* cateArray = @[
                           @[@"5593b3df38dadbed5a998b62", @"5593b3df38dadbed5a998b63", @"5593b7d638dadbed5a998b75", @"5593b88838dadbed5a998b7a", @"5593b88838dadbed5a998b7b", @"5593b88838dadbed5a998b7c", @"5593b88838dadbed5a998b7d", @"5593b88838dadbed5a998b7e", @"5593b88838dadbed5a998b7f", @"5593b88838dadbed5a998b80", @"5593b88838dadbed5a998b81", @"5593b88838dadbed5a998b82"],//上衣
                           @[ @"5593b3df38dadbed5a998b64", @"5593b3df38dadbed5a998b65", @"5593b88838dadbed5a998b83", @"5593b88838dadbed5a998b84", @"5593b88838dadbed5a998b85", @"5593b88838dadbed5a998b86", @"5593b88838dadbed5a998b87", @"5593b88838dadbed5a998b88", @"5593b88838dadbed5a998b89", @"5593b88838dadbed5a998b8a"],//下装
                           @[@"5593b88838dadbed5a998b8c", @"5593b88838dadbed5a998b8e", @"5593b3df38dadbed5a998b66", @"5593b88838dadbed5a998b8b", @"5593b88838dadbed5a998b8d", @"5593b88838dadbed5a998b8f", @"5593b88838dadbed5a998b90"],//连衣裙
                           @[],//内搭
                           @[@"5593b88838dadbed5a998b91", @"5593b88838dadbed5a998b92", @"5593b88838dadbed5a998b93", @"5593b88838dadbed5a998b94", @"5593b88838dadbed5a998b95", @"5593b88838dadbed5a998b96", @"5593b3df38dadbed5a998b67"],//鞋子
                           @[@"5593b88838dadbed5a998b97", @"5593b88838dadbed5a998b98", @"5593b88838dadbed5a998b99", @"5593b88838dadbed5a998b9a", @"5593b88838dadbed5a998b9b", @"5593b3df38dadbed5a998b68"],//包
                           @[@"5593b88838dadbed5a998b9c", @"5593b88838dadbed5a998b9d", @"5593b88838dadbed5a998b9e", @"5593b88838dadbed5a998b9f", @"5593b88838dadbed5a998ba0", @"5593b88838dadbed5a998ba1", @"5593b3df38dadbed5a998b69"]//配饰
                           ];
    for (NSInteger i = 0; i < cateArray.count; i++) {
        NSArray* a = cateArray[i];
        if ([a indexOfObject:categoryId] != NSNotFound) {
            return i;
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
    
    //http://detail.tmall.com/item.htm?spm=a1z10.5-b.w4011-8651694057.389.luXHAj&id=43637272792&rn=7f3fef736d52e1a21ca8c9041f4dc74d&abbucket=9&sku_properties=1627207:28320
    
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
@end
