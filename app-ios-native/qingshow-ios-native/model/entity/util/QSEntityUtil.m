//
//  QSCommonUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSEntityUtil.h"

@implementation QSEntityUtil
+ (BOOL)checkIsNil:(id)obj
{
    return !obj || [obj isKindOfClass:[NSNull class]];
}
+ (BOOL)checkIsDict:(id)obj
{
    if ([self checkIsNil:obj]) {
        return NO;
    }
    return [obj isKindOfClass:[NSDictionary class]];
}
+ (BOOL)checkIsArray:(id)obj
{
    if ([self checkIsNil:obj]) {
        return NO;
    }
    return [obj isKindOfClass:[NSArray class]];
}


+ (NSString*)getIdOrEmptyStr:(NSDictionary*)dict
{
    if (![self checkIsDict:dict]) {
        return @"";
    }
    NSString* idString = dict[@"_id"];
    if ([self checkIsNil:idString]) {
        return @"";
    }
    return idString;
}
+ (NSDictionary*)getResponseContext:(NSDictionary*)dict {
    return [self getDictValue:dict keyPath:@"__context"];
}

+ (id)getValue:(NSDictionary*)dict keyPath:(NSString*)key class:(Class)c {
    if ([self checkIsNil:dict]) {
        return nil;
    }
    id v = [dict valueForKeyPath:key];
    if ([self checkIsNil:v] || ![v isKindOfClass:c]) {
        return nil;
    } else {
        return v;
    }
}

+ (NSString*)getStringValue:(NSDictionary*)dict keyPath:(NSString*)key {
    return [self getValue:dict keyPath:key class:[NSString class]];
}

+ (NSNumber*)getNumberValue:(NSDictionary*)dict keyPath:(NSString*)key {
    return [self getValue:dict keyPath:key class:[NSNumber class]];
}

+ (NSArray*)getArrayValue:(NSDictionary*)dict keyPath:(NSString*)key {
    return [self getValue:dict keyPath:key class:[NSArray class]];
}
+ (NSDictionary*)getDictValue:(NSDictionary*)dict keyPath:(NSString*)key {
    return [self getValue:dict keyPath:key class:[NSDictionary class]];
}
+ (NSDate*)getDateValue:(NSDictionary*)dict keyPath:(NSString*)key {
    return [self getValue:dict keyPath:key class:[NSDate class]];
}


@end
