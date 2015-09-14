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

@implementation QSNetworkEngine(ItemService)

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
