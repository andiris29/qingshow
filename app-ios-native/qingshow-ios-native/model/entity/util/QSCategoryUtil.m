//
//  QSCategoryUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCategoryUtil.h"
#import "QSCommonUtil.h"
@implementation QSCategoryUtil
+ (NSArray*)getChildren:(NSDictionary*)categoryDict {
    return [QSCommonUtil getArrayValue:categoryDict key:@"children"];
}



+ (NSString*)getParentId:(NSDictionary*)categoryDict {
    return [QSCommonUtil getStringValue:categoryDict key:@"parentRef"];
}

+ (BOOL)getMatchEnabled:(NSDictionary*)categoryDict {
    NSNumber* n = [QSCommonUtil getNumberValue:categoryDict key:@"matchInfo.enabled"];
    if (!n) {
        return YES;
    } else {
        return n.boolValue;
    }
   // return n.boolValue;
}

+ (BOOL)getDefaultOnCanvas:(NSDictionary*)categoryDict {
    NSNumber* n = [QSCommonUtil getNumberValue:categoryDict key:@"matchInfo.defaultOnCanvas"];
    return n.boolValue;
}
+ (NSNumber*)getMathchInfoRow:(NSDictionary*)categoryDict {
    return [QSCommonUtil getNumberValue:categoryDict key:@"matchInfo.row"];
}
+ (NSNumber*)getMatchInfoColumn:(NSDictionary*)categoryDict {
    return [QSCommonUtil getNumberValue:categoryDict key:@"matchInfo.column"];
}
+ (NSURL*)getIconUrl:(NSDictionary*)categoryDict{
    NSString* path = [QSCommonUtil getStringValue:categoryDict key:@"icon"];
   // NSLog(@"icon = %@",path);
    NSRange range = [path rangeOfString:@".png"];
    NSString *rangeStr = [path substringToIndex:range.location];
    NSString *imgSelectedUrl = [NSString stringWithFormat:@"%@.png",rangeStr];
    if (path) {
        return [NSURL URLWithString:imgSelectedUrl];
    } else {
        return nil;
    }
}
@end
