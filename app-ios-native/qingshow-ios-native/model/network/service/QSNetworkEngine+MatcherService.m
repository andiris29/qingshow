//
//  QSNetworkEngine+MatcherService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/23/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+Protect.h"
#import "QSNetworkEngine+MatcherService.h"
#import "QSCommonUtil.h"
#import "NSArray+QSExtension.h"
#import "NSDictionary+QSExtension.h"

#define PATH_MATCHER_QUERY_CATEGORIES @"matcher/queryCategories"
#define PATH_MATCHER_QUERY_ITEMS @"matcher/queryItems"
#define PATH_MATCHER_SAVE @"matcher/save"
#define PATH_MATCHER_UPDATE_COVER @"matcher/updateCover"

@implementation QSNetworkEngine(MatcherService)

- (MKNetworkOperation*)matcherQueryCategoriesOnSucceed:(ArraySuccessBlock)succeedBlock
                                               onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_QUERY_CATEGORIES method:@"GET" paramers:@{} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* responseDict = completedOperation.responseJSON;
        if (succeedBlock) {
            succeedBlock([((NSArray*)[responseDict valueForKeyPath:@"data.categories"]) deepMutableCopy], responseDict[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)matcherQueryItemsCategory:(NSDictionary*)categoryDict
                                            page:(int)page
                                       onSucceed:(ArraySuccessBlock)succeedBlock
                                         onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_QUERY_ITEMS method:@"GET" paramers:@{@"_id" : [QSCommonUtil getIdOrEmptyStr:categoryDict]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* responseDict = completedOperation.responseJSON;
        if (succeedBlock) {
            succeedBlock([((NSArray*)[responseDict valueForKeyPath:@"data.items"]) deepMutableCopy], responseDict[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
- (MKNetworkOperation*)matcherSave:(NSArray*)itemArray
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    NSMutableArray* idArray = [@[] mutableCopy];
    for (NSDictionary* itemDict in itemArray) {
        [idArray addObject:[QSCommonUtil getIdOrEmptyStr:itemDict]];
    }
    return [self startOperationWithPath:PATH_MATCHER_SAVE method:@"POST" paramers:@{@"itemRefs" : idArray} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* responseDict = completedOperation.responseJSON;
        if (succeedBlock) {
            succeedBlock([((NSDictionary*)[responseDict valueForKeyPath:@"data.show"]) deepMutableCopy]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)matcher:(NSDictionary*)matcherDict
                   updateCover:(UIImage*)cover
                     onSucceed:(DicBlock)succeedBlock
                       onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_UPDATE_COVER
                                 method:@"POST"
                               paramers:@{@"_id" : [QSCommonUtil getIdOrEmptyStr:matcherDict]}
                                fileKey:@"cover" image:UIImageJPEGRepresentation(cover, 0.5)
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* responseDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    succeedBlock([((NSDictionary*)[responseDict valueForKeyPath:@"data.show"]) deepMutableCopy]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}
@end
