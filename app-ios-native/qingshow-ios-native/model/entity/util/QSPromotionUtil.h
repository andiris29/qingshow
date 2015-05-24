//
//  QSPromotionUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSPromotionUtil : NSObject

+ (NSString*)getHint:(NSDictionary*)dict;
+ (NSString*)getDescription:(NSDictionary*)dict;
+ (NSNumber*)getCriteria:(NSDictionary*)dict;

@end
