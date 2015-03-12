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
    if (![QSCommonUtil checkIsNil:path]) {
        NSURL* url = [NSURL URLWithString:path];
        return url;
    }
    return nil;
}
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
+ (NSString*)getImageDesc:(NSDictionary*)itemDict atIndex:(int)index
{
    NSArray* array = itemDict[@"images"];
    if (index < array.count) {
        NSDictionary* d = array[index];
        return d[@"description"];
    }
    return @"";
}
//+ (NSArray*)getCoverAndImagesUrl:(NSDictionary*)itemDict
//{
//    NSURL* cover = [self getCoverUrl:itemDict];
//    NSArray* imagesUrl = [self getImagesUrl:itemDict];
//    NSMutableArray* m = [@[] mutableCopy];
//    if (cover) {
//        [m addObject:cover];
//    }
//    [m addObjectsFromArray:imagesUrl];
//    return m;
//}

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
        NSString* des = [QSItemUtil getItemName:itemDict];
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

+ (NSArray*)getSkusArray:(NSDictionary*)itemDict
{
    if (![QSCommonUtil checkIsDict:itemDict]) {
        return nil;
    }
    NSDictionary* taobaoInfoDict = itemDict[@"taobaoInfo"];
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
//        return [NSString stringWithFormat:@"￥%.2f-%.2f", minPrice.doubleValue, maxPrice.doubleValue];
        //min(skus[i].price) + ' - ' + max(skus[i].price)
        if (sortedSkus.count == 1 || (ABS(maxPrice.doubleValue - minPrice.doubleValue)) < 0.01) {
            return [NSString stringWithFormat:@"￥%.2f", minPrice.doubleValue];
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

//+ (NSDictionary*)getImageMetaData:(NSDictionary*)itemDict
//{
//    return itemDict[@"imageMetadata"];
//}
//
//+ (CGFloat)getHeight:(NSDictionary*)itemDict
//{
//    NSDictionary* m = [self getImageMetaData:itemDict];
//    return ((NSNumber*)m[@"height"]).floatValue;
//}
//+ (CGFloat)getWidth:(NSDictionary*)itemDict
//{
//    NSDictionary* m = [self getImageMetaData:itemDict];
//    return ((NSNumber*)m[@"width"]).floatValue;
//}
@end
