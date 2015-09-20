//
//  QSHookArray.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSHookArray.h"
#import "QSNetworkEngine+SystemService.h"
@implementation NSArray(Hook)
+ (instancetype)hookArrayWithObjects:(const id [])objects count:(NSUInteger)cnt {
    if (cnt > 0) {
        NSUInteger zeroCount = 0;
        for (NSUInteger index = 0; index < cnt; index++) {
            if (objects[index] == nil) {
                ++zeroCount;
            }
        }

        size_t idSize = sizeof(id);
        NSUInteger newCount = cnt - zeroCount;
        __unsafe_unretained id* copyKeys = (__unsafe_unretained id*)malloc(idSize *  newCount);
        for (NSUInteger i = 0, j = 0; i < cnt; i++) {
            if (objects[i] != nil) {
                copyKeys[j] = objects[i];
                j++;
            }
        }
        NSArray* ret = [self hookArrayWithObjects:copyKeys count:newCount];
        free(copyKeys);
        
        if (zeroCount) {
            [SHARE_NW_ENGINE systemLogLevel:@"error" message:@"Array with nil value" stack:nil extra:[ret description] onSuccess:nil onError:nil];
        }
        
        return ret;
    } else {
        return [self hookArrayWithObjects:objects count:cnt];
    }
}


- (instancetype)initHookWithObjects:(const id [])objects count:(NSUInteger)cnt {
    if (cnt > 0) {
        NSUInteger zeroCount = 0;
        for (NSUInteger index = 0; index < cnt; index++) {
            if (objects[index] == nil) {
                ++zeroCount;
            }
        }
        
        size_t idSize = sizeof(id);
        NSUInteger newCount = cnt - zeroCount;
        __unsafe_unretained id* copyKeys = (__unsafe_unretained id*)malloc(idSize *  newCount);
        for (NSUInteger i = 0, j = 0; i < cnt; i++) {
            if (objects[i] != nil) {
                copyKeys[j] = objects[i];
                j++;
            }
        }
        NSArray* ret = [self initHookWithObjects:copyKeys count:newCount];
        free(copyKeys);
        
        if (zeroCount) {
            [SHARE_NW_ENGINE systemLogLevel:@"error" message:@"Array with nil value" stack:nil extra:[ret description] onSuccess:nil onError:nil];
        }
        
        return ret;
    } else {
        return [self initHookWithObjects:objects count:cnt];
    }

}
@end
