//
//  NSNumber+QSExtension.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "NSNumber+QSExtension.h"

@implementation NSNumber (QSExtension)
@dynamic kmbtStringValue;
- (NSString*)kmbtStringValue
{
    long long num = self.longLongValue;
    NSArray *array = @[@"", @"k", @"m", @"b", @"t"];
    int cur = 0;
    while (num >= 1000) {
        num /= 1000;
        cur++;
    }
    return [NSString stringWithFormat:@"%lld%@",num, array[cur]];
}
@end
