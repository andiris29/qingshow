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
+ (NSURL*)getBrandSloganUrl:(NSDictionary*)dict;
+ (NSString*)getBrandTypeDesc:(NSDictionary*)dict;
+ (BOOL)getHasFollowBrand:(NSDictionary*)dict;
+ (void)setHasFollow:(BOOL)f brand:(NSDictionary*)dict;
@end
