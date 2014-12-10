//
//  QSBrandUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandUtil.h"
#import "QSCommonUtil.h"

@implementation QSBrandUtil

+ (NSString*)getBrandName:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    if ([dict isKindOfClass:[NSDictionary class]]) {
        return dict[@"name"];
    }
    return @"";

}
+ (NSURL*)getBrandLogoUrl:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* s = dict[@"logo"];
    return [NSURL URLWithString:s];
}
+ (NSURL*)getBrandSloganUrl:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* s = dict[@"slogan"];
    return [NSURL URLWithString:s];
}
+ (NSString*)getBrandTypeDesc:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSNumber* type = dict[@"type"];
    return @[@"brand", @"studio"][type.intValue];
}

+ (BOOL)getHasFollowBrand:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return NO;
    }
#warning TODO
    return NO;
}
+ (void)setHasFollow:(BOOL)f brand:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return;
    }
#warning TODO
    
}
@end
