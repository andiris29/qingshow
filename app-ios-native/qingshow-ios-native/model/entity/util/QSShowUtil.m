//
//  QSShowUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowUtil.h"

@implementation QSShowUtil

+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict
{
#warning 暂用cover代替
    NSString* cover = dict[@"cover"];
    if (cover) {
        return @[[NSURL URLWithString:cover]];
    }
    else {
        return nil;
    }
}

+ (NSArray*)getItemsImageUrlArrayFromShow:(NSDictionary*)dict
{
    NSArray* itemArray = dict[@"itemRefs"];
    NSMutableArray* array = [@[] mutableCopy];
    for (NSDictionary* itemDict in itemArray) {
        NSString* path = itemDict[@"cover"];
        NSURL* url = [NSURL URLWithString:path];
        [array addObject:url];
    }
    return array;
}

+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index
{
    NSArray* items = showDict[@"itemRefs"];
    if (items) {
        return items[index];
    }
    
    return nil;
}

@end
