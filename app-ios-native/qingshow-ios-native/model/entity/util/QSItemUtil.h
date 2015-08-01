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
    QSItemCategoryNone = 0,
    QSItemCategoryClothSize = 1,
    QSItemCategoryPant = 2,
    QSItemCategoryShoe = 3
};

@interface QSItemUtil : NSObject

+ (NSArray*)getImagesUrl:(NSDictionary*)itemDict;
+ (NSURL*)getThumbnail:(NSDictionary*)itemDict;
+ (NSURL*)getFirstImagesUrl:(NSDictionary*)itemDict;
+ (NSString*)getItemTypeName:(NSDictionary*)itemDict;
+ (NSURL*)getShopUrl:(NSDictionary*)itemDict;

+ (NSString*)getItemName:(NSDictionary*)item;
+ (NSNumber*)getPrice:(NSDictionary*)itemDict;
+ (NSString*)getPriceDesc:(NSDictionary*)item;
+ (NSNumber*)getPromoPrice:(NSDictionary*)itemDict;
+ (NSString*)getPromoPriceDesc:(NSDictionary*)itemDict;

+ (NSString*)getImageDesc:(NSDictionary*)itemDict atIndex:(int)index;
+ (QSItemCategory)getItemCategory:(NSDictionary*)itemDict;

+ (NSDictionary*)getCategoryRef:(NSDictionary*)itemDict;
+ (NSString*)getCategoryStr:(NSDictionary*)itemDict;
+ (NSArray*)getSkuProperties:(NSDictionary*)itemDict;
+ (NSNumber*)getMinExpectionPrice:(NSDictionary*)itemDict;
@end
