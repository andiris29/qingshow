//
//  NSArray+QSExtension.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "NSArray+QSExtension.h"
#import "NSDictionary+QSExtension.h"
@implementation NSArray(QSExtension)

- (NSMutableArray*)deepMutableCopy
{
    NSMutableArray* a = [@[] mutableCopy];
    for (id obj in self) {
        id v = obj;
        if ([v isKindOfClass:[NSArray class]]) {
            v = [((NSArray*)v) deepMutableCopy];
        } else if ([v isKindOfClass:[NSDictionary class]]) {
            v = [((NSDictionary*)v) deepMutableCopy];
        }
        [a addObject:v];
    }
    return a;
}

- (NSArray*)mapUsingBlock:(IdBlock)block{
    NSMutableArray* retArray = [@[] mutableCopy];
    [self enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [retArray addObject:block(obj)];
    }];
    return retArray;
    
}
- (NSArray*)filteredArrayUsingBlock:(FilterBlock)block {
    NSMutableArray* retArray = [@[] mutableCopy];
    [self enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        if (block(obj)) {
            [retArray addObject:obj];
        }
    }];
    return retArray;
}

- (BOOL)containsAllObjects:(NSArray*)otherArray {
    for (id i in otherArray) {
        if ([self indexOfObject:i] == NSNotFound) {
            return NO;
        }
    }
    return YES;
}
@end
