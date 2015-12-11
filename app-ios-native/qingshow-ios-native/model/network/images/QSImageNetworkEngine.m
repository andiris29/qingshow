//
//  QSImageEngine.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/7.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSImageNetworkEngine.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"
#import "NSFileManager+QSExtension.h"

//100MB
#define MAX_CACHE_FILE_SIZE (100 * 1024 * 1024)

@interface QSImageNetworkEngine()

//Private Method in MKNetworkEngine
@property (nonatomic, strong) NSMutableDictionary *cacheInvalidationParams;
@property (nonatomic, strong) NSMutableDictionary *memoryCache;
@property (nonatomic, strong) NSMutableArray *memoryCacheKeys;

@end
@implementation QSImageNetworkEngine
- (instancetype)init {
    self = [super init];
    if (self) {
        [self useCache];
        [self clearRedundantCache];
    }
    return self;
}

- (void)clearRedundantCache {

    [self _clearExpiredCache];
    
    NSFileManager* fileMgr = [NSFileManager defaultManager];

    long long fSize = [fileMgr folderSizeAtPath:[self cacheDirectoryName]];
    if (fSize > MAX_CACHE_FILE_SIZE) {
        [self _clearUnknownCache];
    }
    fSize = [fileMgr folderSizeAtPath:[self cacheDirectoryName]];
    if (fSize > MAX_CACHE_FILE_SIZE) {
        [self _clearOldestFileWithTotalSize:(fSize - MAX_CACHE_FILE_SIZE)];
    }
}

//Clear Cache This Has Not Been Recored in cacheInvalidationParams
- (void)_clearUnknownCache {
    NSArray *directoryContents = [[NSFileManager defaultManager]
                                  contentsOfDirectoryAtPath:[self cacheDirectoryName] error:NULL];
    for(NSString *fileName in directoryContents) {
        if (![self.cacheInvalidationParams dictValueForKeyPath:fileName]) {
            NSString *path = [[self cacheDirectoryName] stringByAppendingPathComponent:fileName];
            [[NSFileManager defaultManager] removeItemAtPath:path error:NULL];
        }
    }
}

- (void)_clearOldestFileWithTotalSize:(long long)totalSize {
    NSFileManager* fileMgr = [NSFileManager defaultManager];
    NSArray* sortedCacheIds = [[self.cacheInvalidationParams allKeys] sortedArrayUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2) {
        
        NSDate* date1 = [NSDate dateFromRFC1123:[[self.cacheInvalidationParams dictValueForKeyPath:obj1] stringValueForKeyPath:@"Expires"]];
        NSDate* date2 = [NSDate dateFromRFC1123:[[self.cacheInvalidationParams dictValueForKeyPath:obj2] stringValueForKeyPath:@"Expires"]];
        return [date1 compare:date2];
    }];
    //Remove Oldest Cache
    for (NSString* cacheId in sortedCacheIds) {
        NSString *path = [[self cacheDirectoryName] stringByAppendingPathComponent:cacheId];
        totalSize -= [fileMgr fileSizeAtPath:path];
        [self _removeCacheWithId:cacheId];
        if (totalSize <= 0) {
            return;
        }
    }
    
}

- (void)_clearExpiredCache {
    NSDate* now = [NSDate date];
    NSArray* cacheIds = [self.cacheInvalidationParams allKeys];
    for (NSString* cacheId in cacheIds) {
        NSDictionary* attribute = self.cacheInvalidationParams[cacheId];
        NSDate* expireDate = [NSDate dateFromRFC1123:[attribute stringValueForKeyPath:@"Expires"]];
        if ([expireDate laterDate:now] == now) {
            [self _removeCacheWithId:cacheId];
        }
    }
}

- (void)_removeCacheWithId:(NSString*)cacheId {
    NSFileManager* fileMgr = [NSFileManager defaultManager];
    NSString *path = [[self cacheDirectoryName] stringByAppendingPathComponent:cacheId];
    if ([fileMgr fileExistsAtPath:path isDirectory:NULL]) {
        [fileMgr removeItemAtPath:path error:NULL];
    }
    [self.cacheInvalidationParams removeObjectForKey:cacheId];
    [self.memoryCache removeObjectForKey:cacheId];
    [self.memoryCacheKeys removeObject:cacheId];
}
@end
