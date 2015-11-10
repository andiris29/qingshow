//
//  QSCategoryUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//


#import "QSCategoryUtil.h"
#import "QSEntityUtil.h"
#import "NSDictionary+QSExtension.h"

@implementation QSCategoryUtil
+ (NSString*)getName:(NSDictionary*)dict {
//    return [dict stringValueForKeyPath:@"name"];
    return [ QSEntityUtil getStringValue:dict keyPath:@"name"];
}
+ (NSArray*)getChildren:(NSDictionary*)categoryDict {
    return [QSEntityUtil getArrayValue:categoryDict keyPath:@"children"];
}



+ (NSString*)getParentId:(NSDictionary*)categoryDict {
    return [QSEntityUtil getStringValue:categoryDict keyPath:@"parentRef"];
}

+ (BOOL)getMatchEnabled:(NSDictionary*)categoryDict {
    NSNumber* n = [QSEntityUtil getNumberValue:categoryDict keyPath:@"matchInfo.enabled"];
    if (!n) {
        return YES;
    } else {
        return n.boolValue;
    }
   // return n.boolValue;
}

+ (BOOL)getDefaultOnCanvas:(NSDictionary*)categoryDict {
    NSNumber* n = [QSEntityUtil getNumberValue:categoryDict keyPath:@"matchInfo.defaultOnCanvas"];
    return n.boolValue;
}
+ (NSNumber*)getMathchInfoRow:(NSDictionary*)categoryDict {
    return [QSEntityUtil getNumberValue:categoryDict keyPath:@"matchInfo.row"];
}
+ (NSNumber*)getMatchInfoColumn:(NSDictionary*)categoryDict {
    return [QSEntityUtil getNumberValue:categoryDict keyPath:@"matchInfo.column"];
}
+ (NSURL*)getIconUrl:(NSDictionary*)categoryDict{
    NSString* path = [QSEntityUtil getStringValue:categoryDict keyPath:@"icon"];
    if (path) {
        return [NSURL URLWithString:path];
    } else {
        return nil;
    }
}
+ (NSNumber*)getOrder:(NSDictionary*)categoryDict {
    return [QSEntityUtil getNumberValue:categoryDict keyPath:@"order"];
}
@end
