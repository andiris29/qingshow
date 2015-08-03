//
//  QSNetworkEngine+MatcherService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/23/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+Protect.h"
#import "QSNetworkEngine+MatcherService.h"
#import "QSEntityUtil.h"
#import "NSArray+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "QSCategoryUtil.h"
#import "QSCategoryManager.h"

#define PATH_MATCHER_QUERY_CATEGORIES @"matcher/queryCategories"
#define PATH_MATCHER_QUERY_ITEMS @"matcher/queryItems"
#define PATH_MATCHER_SAVE @"matcher/save"
#define PATH_MATCHER_UPDATE_COVER @"matcher/updateCover"
#define PATH_MATCHER_HIDE @"matcher/hide"

@implementation QSNetworkEngine(MatcherService)

- (MKNetworkOperation*)matcherQueryCategoriesOnSucceed:(ArraySuccessBlock)succeedBlock
                                               onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_QUERY_CATEGORIES method:@"GET" paramers:@{} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* responseDict = completedOperation.responseJSON;
        if (succeedBlock) {
            NSArray* resArray = [((NSArray*)[responseDict valueForKeyPath:@"data.categories"]) deepMutableCopy];
            resArray = [resArray sortedArrayUsingComparator:^NSComparisonResult(NSDictionary* obj1, NSDictionary* obj2) {
                return [[QSCategoryUtil getOrder:obj1] compare:[QSCategoryUtil getOrder:obj2]];
            }];
            
            NSMutableArray* retArray = [@[] mutableCopy];
            for (NSDictionary* dict in resArray) {
                if ([QSCategoryUtil getParentId:dict].length == 0) {
                    [retArray addObject:dict];
                    [dict setValue:[@[] mutableCopy] forKey:@"children"];
                }
            }
            
            for (NSDictionary* dict in resArray) {
                if ([QSCategoryUtil getParentId:dict].length != 0){
                    for (NSDictionary* parantDict in retArray) {
                        if ([[QSCategoryUtil getParentId:dict] isEqualToString:[QSEntityUtil getIdOrEmptyStr:parantDict]]) {
                            NSMutableArray* mArray = (NSMutableArray*)[QSCategoryUtil getChildren:parantDict];
                            [mArray addObject:dict];
                        }
                    }
                }
            }
                    
            [QSCategoryManager getInstance].categories = retArray;
            
            succeedBlock(retArray, responseDict[@"metadata"]);
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
    return [self startOperationWithPath:PATH_MATCHER_QUERY_ITEMS method:@"GET" paramers:@{@"categoryRef" : [QSEntityUtil getIdOrEmptyStr:categoryDict], @"pageNo" : @(page), @"pageSize" : @20} onSucceeded:^(MKNetworkOperation *completedOperation) {
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
                         onSucceed:(StringBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    NSMutableArray* idArray = [@[] mutableCopy];
    for (NSDictionary* itemDict in itemArray) {
        [idArray addObject:[QSEntityUtil getIdOrEmptyStr:itemDict]];
    }
    
    
    return [self startOperationWithPath:PATH_MATCHER_SAVE method:@"POST" paramers:@{@"itemRefs" : idArray} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* responseDict = completedOperation.responseJSON;
        
        if (succeedBlock) {
            succeedBlock([responseDict stringValueForKeyPath:@"data.uuid"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)matcherUuid:(NSString*)uuid
                       updateCover:(UIImage*)cover
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    if (!uuid) {
        uuid = @"";
    }
    return [self startOperationWithPath:PATH_MATCHER_UPDATE_COVER
                                 method:@"POST"
                               paramers:@{@"uuid" : uuid}
                                fileKey:@"cover"
                               fileName:@"cover.jpeg"
                                  image:UIImageJPEGRepresentation(cover, 0.7)
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

- (MKNetworkOperation*)matcherHide:(NSDictionary*)matcherDict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_HIDE method:@"POST" paramers:@{@"_id" : [QSEntityUtil getIdOrEmptyStr:matcherDict]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
@end
