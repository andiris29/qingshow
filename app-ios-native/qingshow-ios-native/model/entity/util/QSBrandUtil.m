//
//  QSBrandUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandUtil.h"

@implementation QSBrandUtil

+ (NSString*)getBrandName:(NSDictionary*)dict
{
    return dict[@"name"];
}

@end
