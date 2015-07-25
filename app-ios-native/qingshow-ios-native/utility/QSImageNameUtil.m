//
//  QSImageNameUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 2/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSImageNameUtil.h"

NSString* imageNameTypeToSuf(QSImageNameType type) {
    switch (type) {
        case QSImageNameTypeS:
            return @"_s";
            break;
        case QSImageNameTypeXS:
            return @"_xs";
        case QSImageNameTypeXXS:
            return @"_xxs";
        case QSImageNameTypeXXXS:
            return @"_xxxs";
        case QSImageNameType30:
            return @"_30";
        case QSImageNameType50:
            return @"_50";
        case QSImageNameType100:
            return @"_100";
        case QSImageNameType200:
            return @"_200";
        default:
            return @"";
            break;
    }
}

@implementation QSImageNameUtil

+ (NSURL*)appendImageNameUrl:(NSURL*)imgUrl type:(QSImageNameType)type{
    NSString* urlStr = [imgUrl absoluteString];
    urlStr = [self appendImageName:urlStr type:type];
    return [NSURL URLWithString:urlStr];
}

+ (NSArray*)appendImageNameUrls:(NSArray*)urls type:(QSImageNameType)type
{
    NSMutableArray* retArray = [@[] mutableCopy];
    for (NSURL* url in urls) {
        [retArray addObject:[self appendImageNameUrl:url type:type]];
    }
    return retArray;
}

+ (NSString*)appendImageName:(NSString*)imgName type:(QSImageNameType)type
{
    NSRange range = [imgName rangeOfString:@"." options:NSBackwardsSearch];
    if (range.location == NSNotFound || (imgName.length - range.location) > 5) {
        return [NSString stringWithFormat:@"%@%@",imgName, imageNameTypeToSuf(type)];
    } else {
        return [NSString stringWithFormat:@"%@%@%@",[imgName substringToIndex:range.location], imageNameTypeToSuf(type), [imgName substringFromIndex:range.location]];
        
    }
    return nil;
}

+ (NSArray*)appendImageNames:(NSArray*)strs type:(QSImageNameType)type{
    NSMutableArray* retArray = [@[] mutableCopy];
    for (NSString* name in strs) {
        [retArray addObject:[self appendImageName:name type:type]];
    }
    return retArray;
}

@end
