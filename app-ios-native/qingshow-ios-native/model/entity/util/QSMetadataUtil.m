//
//  QSMetadataUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommonUtil.h"
#import "QSMetadataUtil.h"
#import "NSNumber+QSExtension.h"


@implementation QSMetadataUtil
+ (NSString*)getNumberPageDesc:(NSDictionary*)metadataDict
{
    if ([QSCommonUtil checkIsNil:metadataDict]) {
        return @"0";
    }
    NSNumber* n = metadataDict[@"numPages"];
    if (n) {
        return n.kmbtStringValue;
    }
    return @"0";
}
+ (long long)getNumberTotal:(NSDictionary*)metadataDict
{
    if ([QSCommonUtil checkIsNil:metadataDict]) {
        return 0ll;
    }
    NSNumber* n = metadataDict[@"numTotal"];
    if (n) {
        return n.longLongValue;
    } else {
        return 0ll;
    }
}
+ (void)addTotalNum:(long long )n forDict:(NSDictionary*)metadataDict
{
    if (![metadataDict isKindOfClass:[NSMutableDictionary class]]) {
        return ;
    }
    NSMutableDictionary* m = (NSMutableDictionary*)metadataDict;
    long long num = [self getNumberTotal:metadataDict];
    num += n;
    num = num >= 0ll ? num : 0;
    m[@"numTotal"] = @(num);
}
+ (NSString*)getNumberTotalDesc:(NSDictionary*)metadataDict
{
    if ([QSCommonUtil checkIsNil:metadataDict]) {
        return @"0";
    }
    NSNumber* n = metadataDict[@"numTotal"];
    if (n) {
        return n.kmbtStringValue;
    }
    return @"0";
}
@end
