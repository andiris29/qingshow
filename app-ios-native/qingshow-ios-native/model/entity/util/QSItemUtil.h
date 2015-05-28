//
//  QSItemUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, QSItemCategory) {
    QSItemCategoryUnknown = -1,
    QSItemCategoryShangyi = 0,
    QSItemCategoryPant = 1,
    QSItemCategoryDress = 2,
    QSItemCategoryNeida = 3,
    QSItemCategoryShoe = 4,
    QSItemCategoryBag = 5,
    QSItemCategoryPeishi = 6,
    QSItemCategoryCount
};

@interface QSItemUtil : NSObject

+ (NSArray*)getImagesUrl:(NSDictionary*)itemDict;
+ (NSURL*)getFirstImagesUrl:(NSDictionary*)itemDict;
+ (NSString*)getItemTypeName:(NSDictionary*)itemDict;
+ (NSURL*)getShopUrl:(NSDictionary*)itemDict;

+ (NSString*)getItemName:(NSDictionary*)item;
+ (BOOL)hasDiscountInfo:(NSDictionary*)item;
+ (NSString*)getPrice:(NSDictionary*)item;
+ (NSString*)getPriceAfterDiscount:(NSDictionary*)itemDict;

+ (NSString*)getImageDesc:(NSDictionary*)itemDict atIndex:(int)index;

+ (NSDictionary*)getTaobaoInfo:(NSDictionary*)item;

+ (NSString*)getVideoPath:(NSDictionary*)item;
+ (NSString*)getSelectedSku:(NSDictionary*)item;


+ (BOOL)getIsLike:(NSDictionary*)itemDict;
+ (void)setIsLike:(BOOL)isLike item:(NSDictionary*)itemDict;
+ (void)addNumberLike:(long long)num forItem:(NSDictionary*)itemDict;
+ (NSString*)getNumberLikeDescription:(NSDictionary*)itemDict;

+ (NSDate*)getLikeDate:(NSDictionary*)itemDict;
+ (QSItemCategory)getItemCategory:(NSDictionary*)itemDict;

+ (NSString*)getItemColorDesc:(NSDictionary*)itemDict;

@end
