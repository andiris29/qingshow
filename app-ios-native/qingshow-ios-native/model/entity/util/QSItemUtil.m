//
//  QSItemUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemUtil.h"

@implementation QSItemUtil
+ (NSURL*)getCoverUrl:(NSDictionary*)itemDict
{
    NSString* path = itemDict[@"cover"];
    if (path) {
        NSURL* url = [NSURL URLWithString:path];
        return url;
    }
    return nil;
}
+ (NSURL*)getShopUrl:(NSDictionary*)itemDict
{
    NSString* path = itemDict[@"source"];
    if (path) {
        NSURL* url = [NSURL URLWithString:path];
        return url;
    }
    return nil;
}
@end
