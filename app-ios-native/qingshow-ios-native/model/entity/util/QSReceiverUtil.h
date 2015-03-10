//
//  QSReceiverUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSReceiverUtil : NSObject

+ (NSString*)getName:(NSDictionary*)dict;
+ (NSString*)getPhone:(NSDictionary*)dict;
+ (NSString*)getProvince:(NSDictionary*)dict;
+ (NSString*)getAddress:(NSDictionary*)dict;
+ (NSString*)getDefault:(NSDictionary*)dict;

@end
