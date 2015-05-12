//
//  QSNetworkEngine+ItemService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+ItemService.h"
#import "QSNetworkEngine+Protect.h"
#import "QSItemUtil.h"
#import "QSError.h"

#define PATH_ITEM_LIKE @"item/like"
#define PATH_ITEM_UNLIKE @"item/unlike"

@implementation QSNetworkEngine(ItemService)

#pragma mark - Like
- (MKNetworkOperation*)handleItemLike:(NSDictionary*)itemDict
                            onSucceed:(BoolBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    if ([QSItemUtil getIsLike:itemDict]) {
        op = [self unlikeItem:itemDict onSucceed:^{
            succeedBlock(NO);
        } onError:errorBlock];
    } else {
        op = [self likeItem:itemDict onSucceed:^{
            succeedBlock(YES);
        } onError:errorBlock];
    }
    return op;
}

- (MKNetworkOperation*)likeItem:(NSDictionary*)itemDict
                      onSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_ITEM_LIKE method:@"POST" paramers:@{@"_id" : itemDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [QSItemUtil setIsLike:YES item:itemDict];
        [QSItemUtil addNumberLike:1ll forItem:itemDict];
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if ([error isKindOfClass:[QSError class]]) {
            if (error.code == kQSErrorCodeAlreadyFollow) {
                [QSItemUtil setIsLike:YES item:itemDict];
            } else if (error.code == kQSErrorCodeAlreadyUnfollow) {
                [QSItemUtil setIsLike:NO item:itemDict];
            }
        }
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
- (MKNetworkOperation*)unlikeItem:(NSDictionary*)showDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_ITEM_UNLIKE method:@"POST" paramers:@{@"_id" : showDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [QSItemUtil setIsLike:NO item:showDict];
        [QSItemUtil addNumberLike:-1ll forItem:showDict];
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if ([error isKindOfClass:[QSError class]]) {
            if (error.code == kQSErrorCodeAlreadyFollow) {
                [QSItemUtil setIsLike:YES item:showDict];
            } else if (error.code == kQSErrorCodeAlreadyUnfollow) {
                [QSItemUtil setIsLike:NO item:showDict];
            }
        }
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
@end
