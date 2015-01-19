//
//  QSCommonUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommonUtil.h"

@implementation QSCommonUtil
+ (BOOL)checkIsNil:(id)obj
{
    return !obj || [obj isKindOfClass:[NSNull class]];
}
+ (BOOL)checkIsDict:(id)obj
{
    return [obj isKindOfClass:[NSDictionary class]];
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
@end
