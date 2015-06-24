//
//  QSCommonUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSCommonUtil : NSObject

+ (BOOL)checkIsNil:(id)obj;
+ (BOOL)checkIsDict:(id)obj;
+ (BOOL)checkIsArray:(id)obj;
+ (NSString*)getIdOrEmptyStr:(NSDictionary*)dict;
+ (NSString *)getCommentsStr:(NSDictionary *)dict;


+ (NSString*)getStringValue:(NSDictionary*)dict key:(NSString*)key;
+ (NSArray*)getArrayValue:(NSDictionary*)dict key:(NSString*)key;
@end
