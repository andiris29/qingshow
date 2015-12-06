//
//  QSNetworkEngine+MatcherService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/23/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "MKNetworkEngine+QSExtension.h"
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
#define PATH_MATCHER_REMIX_BY_ITEM @"matcher/remixByItem"
#define PATH_MATCHER_REMIX_BY_MODEL @"matcher/remixByModel"

@implementation QSNetworkEngine(MatcherService)

- (MKNetworkOperation*)matcherQueryCategoriesOnSucceed:(QueryCategorySucceedBlock)succeedBlock
                                               onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_QUERY_CATEGORIES method:@"GET" paramers:@{} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* responseDict = completedOperation.responseJSON;
        if (succeedBlock) {
            NSArray* resArray = [[responseDict arrayValueForKeyPath:@"data.categories"] deepMutableCopy];
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
            
//            modelCategoryRef
            NSDictionary* metadata = [responseDict dictValueForKeyPath:@"metadata"];
            NSString* modelCategoryId = [metadata stringValueForKeyPath:@"modelCategoryRef"];
            NSDictionary* modelCategory = [[QSCategoryManager getInstance] findCategoryOfId:modelCategoryId];
            succeedBlock(retArray, modelCategory, metadata);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)matcherQueryItemsCategoryId:(NSString*)categoryId
                                              page:(int)page
                                         onSucceed:(ArraySuccessBlock)succeedBlock
                                           onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_QUERY_ITEMS method:@"GET" paramers:@{@"categoryRef" : categoryId, @"pageNo" : @(page), @"pageSize" : @20} onSucceeded:^(MKNetworkOperation *completedOperation) {
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

- (MKNetworkOperation*)matcherQueryItemsCategory:(NSDictionary*)categoryDict
                                            page:(int)page
                                       onSucceed:(ArraySuccessBlock)succeedBlock
                                         onError:(ErrorBlock)errorBlock {
    return [self matcherQueryItemsCategoryId:[QSEntityUtil getIdOrEmptyStr:categoryDict]
                                        page:page
                                   onSucceed:succeedBlock
                                     onError:errorBlock];
}
- (MKNetworkOperation*)matcherSave:(NSArray*)itemArray
                         itemRects:(NSArray*)itemRects
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    NSMutableArray* idArray = [@[] mutableCopy];
    for (NSDictionary* itemDict in itemArray) {
        [idArray addObject:[QSEntityUtil getIdOrEmptyStr:itemDict]];
    }
    
    
    return [self startOperationWithPath:PATH_MATCHER_SAVE
                                 method:@"POST"
                               paramers:@{
                                          @"itemRefs" : idArray,
                                          @"itemRects" : itemRects}
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)matcherUpdateCover:(UIImage*)cover
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_UPDATE_COVER
                                 method:@"POST"
                               paramers:@{}
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

- (MKNetworkOperation*)matcherRemixByModel:(NSString*)modelId
                                 onSucceed:(DicBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_REMIX_BY_MODEL method:@"GET" paramers:@{@"modelRef" : modelId} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* dict = completedOperation.responseJSON;
        NSDictionary* data = [dict dictValueForKeyPath:@"data"];
        
        if (succeedBlock) {
            succeedBlock(data);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}


- (MKNetworkOperation*)matcherRemixByItem:(NSDictionary*)itemDict
                                    cache:(BOOL)cache
                                onSucceed:(DicBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:PATH_MATCHER_REMIX_BY_ITEM
                                 method:@"GET"
                               paramers:@{
                                          @"itemRef" : [QSEntityUtil getIdOrEmptyStr:itemDict],
                                          @"cache" : @(cache)
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    succeedBlock([completedOperation.responseJSON dictValueForKeyPath:@"data"]);
                                }
    }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
    }];
}
@end
