//
//  QSBrandUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSBrandUtil : NSObject

+ (NSString*)getBrandName:(NSDictionary*)dict;
+ (NSURL*)getBrandLogoUrl:(NSDictionary*)dict;

+ (NSURL*)getBrandBgUrl:(NSDictionary*)dict;
+ (NSURL*)getBrandCoverUrl:(NSDictionary*)dict;
+ (NSString*)getBrandShopPhone:(NSDictionary*)dict;
+ (NSString*)getBrandShopAddress:(NSDictionary*)dict;

+ (BOOL)getHasFollowBrand:(NSDictionary*)dict;
+ (void)setHasFollow:(BOOL)f brand:(NSDictionary*)dict;
@end
