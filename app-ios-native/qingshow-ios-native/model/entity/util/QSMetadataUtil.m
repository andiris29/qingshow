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
