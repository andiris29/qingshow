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
+ (NSString*)getPriceDesc:(NSDictionary*)dict;
+ (NSString*)getQuantityDesc:(NSDictionary*)dict;
+ (NSString*)getSkuId:(NSDictionary*)dict;
+ (NSString*)getReceiverUuid:(NSDictionary*)dict;

@end
