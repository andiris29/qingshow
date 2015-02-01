//
//  QSNetworkEngine+ItemFeedingService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSNetworkEngine+Protect.h"
#import "QSNetworkEngine+ItemFeedingService.h"
#import "NSArray+QSExtension.h"
#import "QSCommonUtil.h"

#define ITEM_FEEDING_BY_BRAND_NEW @"i6temFeeding/byBrandNew"
#define ITEM_FEEDING_BY_BRAND_DISCOUNT @"itemFeeding/byBrandDiscount"
#define ITEM_FEEDING_RANDOM @"itemFeeding/random"

@implementation QSNetworkEngine(ItemFeedingService)
- (MKNetworkOperation*)getItemFeedingByBrandNew:(NSDictionary*)brand
                                           page:(int)page
                                      onSucceed:(ArraySuccessBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:ITEM_FEEDING_BY_BRAND_NEW
                                 method:@"GET"
                               paramers:@{@"pageNo":@(page), @"pageSize": @10, @"_id": [QSCommonUtil getIdOrEmptyStr:brand]}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    NSArray* items = retDict[@"data"][@"items"];
                    succeedBlock([items deepMutableCopy], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];

}
- (MKNetworkOperation*)getItemFeedingByBrandDiscount:(NSDictionary*)brand
                                                page:(int)page
                                           onSucceed:(ArraySuccessBlock)succeedBlock
                                             onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:ITEM_FEEDING_BY_BRAND_DISCOUNT
                                 method:@"GET"
                               paramers:@{@"pageNo":@(page), @"pageSize": @10, @"_id": [QSCommonUtil getIdOrEmptyStr:brand]}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    NSArray* items = retDict[@"data"][@"items"];
                    succeedBlock([items deepMutableCopy], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation*)getItemFeedingRandomPage:(int)page
                                      onSucceed:(ArraySuccessBlock)successBlock
                                        onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:ITEM_FEEDING_RANDOM
                                 method:@"GET"
                               paramers:@{@"pageNo":@(page), @"pageSize": @10}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (successBlock) {
                    NSArray* items = retDict[@"data"][@"items"];
                    successBlock([items deepMutableCopy], retDict[@"metadata"]);
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
