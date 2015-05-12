//
//  QSImageNameUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 2/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSImageNameUtil.h"

@implementation QSImageNameUtil
+ (NSURL*)generate2xImageNameUrl:(NSURL*)imgUrl
{
    NSString* str = [imgUrl absoluteString];
    NSString* str2x = [self generate2xImageName:str];
    return [NSURL URLWithString:str2x];
}
+ (NSArray*)generate2xImageNameUrlArray:(NSArray*)imgUrl
{
    NSMutableArray* retArray = [@[] mutableCopy];
    
    [imgUrl enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSURL* url = (NSURL*)obj;
        [retArray addObject:[self generate2xImageNameUrl:url]];
    }];
    return retArray;
}
+ (NSString*)generate2xImageName:(NSString*)imgName
{
    NSRange range = [imgName rangeOfString:@"." options:NSBackwardsSearch];
    if (range.location == NSNotFound) {
        return [NSString stringWithFormat:@"%@@2x",imgName];
    } else {
        return [NSString stringWithFormat:@"%@@2x%@",[imgName substringToIndex:range.location], [imgName substringFromIndex:range.location]];
        
    }
}
+ (NSArray*)generate2xImageNameArray:(NSArray*)imgNames
{
    NSMutableArray* arr = [@[] mutableCopy];
    [imgNames enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSString* str = (NSString*)obj;
        NSString* str2x = [self generate2xImageName:str];
        [arr addObject:str2x];
    }];
    return arr;
}
@end
