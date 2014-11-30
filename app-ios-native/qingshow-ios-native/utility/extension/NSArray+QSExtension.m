//
//  NSArray+QSExtension.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "NSArray+QSExtension.h"

@implementation NSArray(QSExtension)

- (NSMutableArray*)deepDictMutableCopy
{
    NSMutableArray* a = [@[] mutableCopy];
    for (NSDictionary* dict in self) {
        [a addObject:[dict mutableCopy]];
    }
    return a;
}

@end
