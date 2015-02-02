//
//  QSNetworkEngine+BrandService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+BrandService.h"
#import "QSNetworkEngine+Protect.h"
#import "QSBrandUtil.h"
#import "NSArray+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "QSCommonUtil.h"
#import "NSMutableDictionary+QSExtension.h"
#import "QSError.h"


//Path
#define PATH_PEOPLE_FOLLOW_BRAND @"brand/follow"
#define PATH_PEOPLE_UNFOLLOW_BRAND @"brand/unfollow"
#define PATH_QUERY_BRAND @"brand/queryBrands"
#define PATH_QUERY_BRAND_FOLLOWER @"brand/queryFollowers"
#define PATH_QUARY_BRAND_DETAIL @"brand/query"

@implementation QSNetworkEngine(BrandService)
#pragma mark - Query
- (MKNetworkOperation*)queryBrands:(NSNumber*)type
                              page:(int)page
                         onSucceed:(ArraySuccessBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{@"pageNo": @(page), @"pageSize" : @10} mutableCopy];
    if (type) {
        paramDict[@"type"] = type;
    }
    return [self startOperationWithPath:PATH_QUERY_BRAND method:@"GET" paramers:paramDict onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* retDict = completedOperation.responseJSON;
        NSArray* retArray = retDict[@"data"][@"brands"];
        if (succeedBlock) {
            succeedBlock([retArray deepMutableCopy], retDict[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)queryBrandsDetail:(NSDictionary*)brandDict
                               onSucceed:(DicBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    NSString* brandId = [QSCommonUtil getIdOrEmptyStr:brandDict];
    NSDictionary* paramDict = @{@"_ids" : brandId};
    return [self startOperationWithPath:PATH_QUARY_BRAND_DETAIL method:@"GET" paramers:paramDict onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* retDict = completedOperation.responseJSON;
        NSArray* brands = retDict[@"data"][@"brands"];
        
        NSDictionary* b = nil;
        if (brands.count) {
            b = brands[0];
        }

        NSMutableDictionary* mB = [b deepMutableCopy];
        if ([brandDict isKindOfClass:[NSMutableDictionary class]]) {
            NSMutableDictionary* d = (NSMutableDictionary*)brandDict;
            [d updateWithDict:mB];
        }
        
        if (succeedBlock) {
            succeedBlock(brandDict);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)queryBrandFollower:(NSDictionary*)brandDict
                                     page:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_QUERY_BRAND_FOLLOWER method:@"GET" paramers:@{@"_id" : [QSCommonUtil getIdOrEmptyStr:brandDict], @"pageNo": @(page), @"pageSize" : @10} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* retDict = completedOperation.responseJSON;
        NSArray* retArray = retDict[@"data"][@"peoples"];
        if (succeedBlock) {
            succeedBlock([retArray deepMutableCopy], retDict[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

#pragma mark - Follow
- (MKNetworkOperation*)handleFollowBrand:(NSDictionary*)brandDict
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    
    if ([QSBrandUtil getHasFollowBrand:brandDict]) {
        return [self unfollowBrand:brandDict onSucceed:^{
            [QSBrandUtil setHasFollow:NO brand:brandDict];
            if (succeedBlock) {
                succeedBlock(NO);
            }
        } onError:^(NSError *error) {
            if ([error isKindOfClass:[QSError class]]) {
                if (error.code == kQSErrorCodeAlreadyFollow) {
                    [QSBrandUtil setHasFollow:YES brand:brandDict];
                } else if (error.code == kQSErrorCodeAlreadyUnfollow) {
                    [QSBrandUtil setHasFollow:NO brand:brandDict];
                }
            }
            if (errorBlock) {
                errorBlock(error);
            }
        }];
    }
    else
    {
        
        return [self followBrand:brandDict onSucceed:^{
            [QSBrandUtil setHasFollow:YES brand:brandDict];
            if (succeedBlock) {
                succeedBlock(YES);
            }
        } onError:errorBlock];
    }
}
- (MKNetworkOperation*)followBrand:(NSDictionary*)brandDict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PEOPLE_FOLLOW_BRAND
                                 method:@"POST" paramers:@{@"_id" : brandDict[@"_id"]}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    succeedBlock();
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation*)unfollowBrand:(NSDictionary*)brandDict
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PEOPLE_UNFOLLOW_BRAND
                                 method:@"POST"
                               paramers:@{@"_id" : brandDict[@"_id"]}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                if (succeedBlock) {
                    succeedBlock();
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
