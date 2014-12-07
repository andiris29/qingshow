//
//  QSMetadataUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSMetadataUtil.h"
#import "NSNumber+QSExtension.h"


@implementation QSMetadataUtil
+ (NSString*)getNumberPageDesc:(NSDictionary*)metadataDict
{
    NSNumber* n = metadataDict[@"numPages"];
    if (n) {
        return n.kmbtStringValue;
    }
    return @"0";
}
+ (NSString*)getNumberTotalDesc:(NSDictionary*)metadataDict
{
    NSNumber* n = metadataDict[@"numTotal"];
    if (n) {
        return n.kmbtStringValue;
    }
    return @"0";
}
@end
