//
//  QSShowUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowUtil.h"
#import "QSItemUtil.h"
@implementation QSShowUtil

+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict
{
    NSArray* posters = dict[@"posters"];
    NSMutableArray* urlArray = [@[] mutableCopy];
    if (posters && posters.count) {
        for (NSString* path in posters) {
            [urlArray addObject:[NSURL URLWithString:path]];
        }
        return urlArray;
    }
    else {
        return nil;
    }
}

+ (NSArray*)getItemsImageUrlArrayFromShow:(NSDictionary*)dict
{
    
    NSArray* itemArray = dict[@"itemRefs"];
    return [QSItemUtil getItemsImageUrlArray:itemArray];
}
+ (NSArray*)getItems:(NSDictionary *)showDict
{
    return showDict[@"itemRefs"];
}

+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index
{
    NSArray* items = showDict[@"itemRefs"];
    if (items) {
        return items[index];
    }
    
    return nil;
}
+ (NSDictionary*)getPeopleFromShow:(NSDictionary*)showDict
{
    if (showDict) {
        return showDict[@"modelRef"];
    }
    return nil;
}

+ (NSString*)getNumberCommentsDescription:(NSDictionary*)showDict
{
    NSNumber* n = showDict[@"$numComments"];
    if (!n) {
        return @"0";
    }
    return n.stringValue;
}
+ (NSString*)getNumberFavorDescription:(NSDictionary*)showDict
{
    NSNumber* n = showDict[@"$numFavors"];
    if (!n) {
        return @"0";
    }
    return n.stringValue;
}
+ (BOOL)getIsLike:(NSDictionary*)showDict
{
    NSNumber* n = showDict[@"isLiked"];
    return n.boolValue;
}
@end
