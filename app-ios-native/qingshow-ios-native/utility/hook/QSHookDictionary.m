//
//  QSHookDictionary.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSHookDictionary.h"

@implementation NSDictionary (QSHookDictionary)

+ (instancetype)hookDictionaryWithObjects:(const id [])objects forKeys:(const id <NSCopying> [])keys count:(NSUInteger)cnt{
    if (cnt > 0) {
        NSUInteger nilCount = 0;
        for (NSUInteger i = 0; i < cnt; i++) {
            if (objects[i] == nil) {
#warning TODO Add network log
                ++nilCount;
            }
        }
        
        size_t idSize = sizeof(id);
        NSUInteger count = cnt - nilCount;
        __unsafe_unretained id* copyKeys = (__unsafe_unretained id*)malloc(idSize *  count);
        __unsafe_unretained id* copyObjects = (__unsafe_unretained id*)malloc(idSize *  count);
        
        for (NSUInteger i = 0, j = 0; i < cnt; i++) {
            if (objects[i] != nil) {
                copyKeys[j] = keys[i];
                copyObjects[j] = objects[i];
                j++;
            }
        }
        id d = [self hookDictionaryWithObjects:copyObjects forKeys:copyKeys count:count];
        free(copyKeys);
        copyKeys = NULL;
        free(copyObjects);
        copyObjects = NULL;
        
        return d;
    } else {
        return [self hookDictionaryWithObjects:objects forKeys:keys count:cnt];
    }
}

- (instancetype)initHookWithObjects:(const id [])objects forKeys:(const id<NSCopying> [])keys count:(NSUInteger)cnt {
    if (cnt > 0) {
        NSUInteger nilCount = 0;
        for (NSUInteger i = 0; i < cnt; i++) {
            if (objects[i] == nil) {
#warning TODO Add network log
                ++nilCount;
            }
        }
        size_t idSize = sizeof(id);
        NSUInteger count = cnt - nilCount;
        __unsafe_unretained id* copyKeys = (__unsafe_unretained id*)malloc(idSize *  count);
        __unsafe_unretained id* copyObjects = (__unsafe_unretained id*)malloc(idSize *  count);
        
        for (NSUInteger i = 0, j = 0; i < cnt; i++) {
            if (objects[i] != nil) {
                copyKeys[j] = keys[i];
                copyObjects[j] = objects[i];
                j++;
            }
        }
        id d = [self initHookWithObjects:copyObjects forKeys:copyKeys count:count];
        free(copyKeys);
        copyKeys = NULL;
        free(copyObjects);
        copyObjects = NULL;
        return d;
    } else {
        return [self initHookWithObjects:objects forKeys:keys count:cnt];
    }
}
@end
