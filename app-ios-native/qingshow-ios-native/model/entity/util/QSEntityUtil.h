//
//  QSCommonUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSEntityUtil : NSObject

+ (BOOL)checkIsNil:(id)obj;
+ (BOOL)checkIsDict:(id)obj;
+ (BOOL)checkIsArray:(id)obj;

+ (NSString*)getIdOrEmptyStr:(NSDictionary*)dict;
+ (NSDictionary*)getResponseContext:(NSDictionary*)dict;

+ (NSString*)getStringValue:(NSDictionary*)dict keyPath:(NSString*)key;
+ (NSArray*)getArrayValue:(NSDictionary*)dict keyPath:(NSString*)key;
+ (NSDictionary*)getDictValue:(NSDictionary*)dict keyPath:(NSString*)key;
+ (NSNumber*)getNumberValue:(NSDictionary*)dict keyPath:(NSString*)key;
+ (NSDate*)getDateValue:(NSDictionary*)dict keyPath:(NSString*)key;
@end
