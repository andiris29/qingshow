//
//  QSItemUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSItemUtil : NSObject

+ (NSURL*)getCoverUrl:(NSDictionary*)itemDict;
+ (NSArray*)getImagesUrl:(NSDictionary*)itemDict;
//+ (NSArray*)getCoverAndImagesUrl:(NSDictionary*)itemDict;

+ (NSURL*)getShopUrl:(NSDictionary*)itemDict;


+ (NSAttributedString*)getItemsAttributedDescription:(NSArray*)itemsArray;
+ (NSString*)getItemName:(NSDictionary*)item;
+ (NSString*)getItemTypeName:(NSDictionary*)item;
+ (BOOL)hasDiscountInfo:(NSDictionary*)item;
+ (NSString*)getPrice:(NSDictionary*)item;
+ (NSString*)getPriceAfterDiscount:(NSDictionary*)itemDict;

+ (NSDictionary*)getBrand:(NSDictionary*)item;
+ (NSArray*)getItemsImageUrlArray:(NSArray*)itemArray;
+ (NSString*)getImageDesc:(NSDictionary*)itemDict atIndex:(int)index;

+ (NSDictionary*)getTaobaoInfo:(NSDictionary*)item;

//+ (CGFloat)getHeight:(NSDictionary*)itemDict;
@end
