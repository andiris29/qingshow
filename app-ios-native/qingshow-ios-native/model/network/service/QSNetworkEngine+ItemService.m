//
//  QSNetworkEngine+ItemService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 9/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+ItemService.h"
#import "MKNetworkEngine+QSExtension.h"
#import "NSDictionary+QSExtension.h"

#define URL_ITEM_SYNC @"item/sync"
#define PATH_ITEM_QUERY @"item/query"

@implementation QSNetworkEngine(ItemService)
#pragma mark - Item
- (MKNetworkOperation*)getItemWithId:(NSString*)itemId
                           onSucceed:(EntitySuccessBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_ITEM_QUERY method:@"GET" paramers:@{@"_ids":itemId} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary *retDic = completedOperation.responseJSON;
            NSArray *array = retDic[@"data"][@"items"];
            NSDictionary* dict = [array firstObject];
            succeedBlock([dict deepMutableCopy],retDic[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)itemSync:(NSString*)itemId
                      onSucceed:(EntitySuccessBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock {
    return [self startOperationWithPath:URL_ITEM_SYNC
                                 method:@"POST"
                               paramers:@{
                                          @"_id" : itemId
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation) {
                                if (succeedBlock) {
                                    NSDictionary* retDict = completedOperation.responseJSON;
                                    NSDictionary* itemDict = [retDict dictValueForKeyPath:@"data.item"];
                                    succeedBlock(itemDict, [retDict dictValueForKeyPath:@"metadata"]);
                                }
                            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error) {
                                    if (errorBlock) {
                                        errorBlock(error);
                                    }
                                }];
}
@end
