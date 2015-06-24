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
@end
