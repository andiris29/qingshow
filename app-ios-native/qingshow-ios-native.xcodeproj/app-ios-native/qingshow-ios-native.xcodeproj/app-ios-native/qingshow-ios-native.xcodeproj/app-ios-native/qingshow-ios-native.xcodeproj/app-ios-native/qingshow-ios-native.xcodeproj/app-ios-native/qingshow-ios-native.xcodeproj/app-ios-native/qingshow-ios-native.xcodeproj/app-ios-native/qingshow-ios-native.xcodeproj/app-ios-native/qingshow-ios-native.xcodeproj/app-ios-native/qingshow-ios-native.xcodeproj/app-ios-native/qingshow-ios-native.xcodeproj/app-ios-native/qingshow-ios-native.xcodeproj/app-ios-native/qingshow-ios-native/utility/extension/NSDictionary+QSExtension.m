//
//  NSDictionary+QSExtension.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"
@implementation NSDictionary(QSExtension)

- (NSMutableDictionary*)deepMutableCopy;
{
    NSMutableDictionary* dict = [@{} mutableCopy];
    for (NSString* key in self.allKeys) {
        id value = self[key];
        if ([value isKindOfClass:[NSDictionary class]]) {
            value = [((NSDictionary*)value) deepMutableCopy];
        } else if ([value isKindOfClass:[NSArray class]]) {
            value = [((NSArray*)value) deepMutableCopy];
        }
        dict[key] = value;
    }
    
    return dict;
}
@end
