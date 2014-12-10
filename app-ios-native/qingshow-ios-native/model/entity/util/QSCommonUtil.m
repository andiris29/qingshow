//
//  QSCommonUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommonUtil.h"

@implementation QSCommonUtil
+ (BOOL)checkIsNil:(id)obj
{
    return !obj || [obj isKindOfClass:[NSNull class]];
}
@end
