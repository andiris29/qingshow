//
//  NSMutableDictionary+QSExtension.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/25/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "NSMutableDictionary+QSExtension.h"
#import "NSDictionary+QSExtension.h"
@implementation NSMutableDictionary(QSExtension)

- (void)updateWithDict:(NSDictionary*)dict
{
    dict = [dict deepMutableCopy];
    for (id key in [dict allKeys]) {
        self[key] = dict[key];
    }
}

@end
