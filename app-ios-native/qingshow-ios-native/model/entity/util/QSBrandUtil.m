//
//  QSBrandUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandUtil.h"

@implementation QSBrandUtil

+ (NSString*)getBrandName:(NSDictionary*)dict
{
    if ([dict isKindOfClass:[NSDictionary class]]) {
        return dict[@"name"];
    }
    return @"";

}
+ (NSURL*)getBrandLogoUrl:(NSDictionary*)dict
{
    NSString* s = dict[@"logo"];
    return [NSURL URLWithString:s];
}
+ (NSURL*)getBrandSloganUrl:(NSDictionary*)dict
{
    NSString* s = dict[@"slogan"];
    return [NSURL URLWithString:s];
}
+ (NSString*)getBrandTypeDesc:(NSDictionary*)dict
{
    NSNumber* type = dict[@"type"];
    return @[@"brand", @"studio"][type.intValue];
}

+ (BOOL)getHasFollowBrand:(NSDictionary*)dict
{
#warning TODO
    return NO;
}
+ (void)setHasFollow:(BOOL)f brand:(NSDictionary*)dict
{
#warning TODO
    
}
@end
