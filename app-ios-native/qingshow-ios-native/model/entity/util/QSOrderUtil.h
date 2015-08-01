//
//  QSOrderUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSOrderUtil : NSObject

+ (NSDictionary*)getItemSnapshot:(NSDictionary*)dict;
+ (NSArray*)getSkuProperties:(NSDictionary*)dict;
+ (NSNumber*)getExpectedPrice:(NSDictionary*)dict;
+ (NSString*)getExpectedPriceDesc:(NSDictionary*)dict;
+ (NSNumber*)getActualPrice:(NSDictionary*)dict;
+ (NSString*)getActualPriceDesc:(NSDictionary*)dict;
+ (NSNumber*)getQuantity:(NSDictionary*)dict;
+ (NSString*)getQuantityDesc:(NSDictionary*)dict;

+ (NSString*)getReceiverUuid:(NSDictionary*)dict;

@end
