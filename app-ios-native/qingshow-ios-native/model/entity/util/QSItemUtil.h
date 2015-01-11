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
+ (NSURL*)getShopUrl:(NSDictionary*)itemDict;
+ (NSAttributedString*)getItemsAttributedDescription:(NSArray*)itemsArray;
+ (NSString*)getItemDescription:(NSDictionary*)item;
+ (NSString*)getItemTypeName:(NSDictionary*)item;
+ (NSString*)getPrice:(NSDictionary*)item;
+ (NSString*)getPriceAfterDiscount:(NSDictionary*)itemDict;

+ (NSDictionary*)getBrand:(NSDictionary*)item;
+ (NSArray*)getItemsImageUrlArray:(NSArray*)itemArray;
+ (NSURL*)getIconUrl:(NSDictionary*)itemDict;
@end
