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

//Path
#define PATH_PEOPLE_FOLLOW_BRAND @"brand/follow"
#define PATH_PEOPLE_UNFOLLOW_BRAND @"brand/unfollow"
#define PATH_QUERY_BRAND @"brand/queryBrands"
#define PATH_QUERY_BRAND_FOLLOWER @"brand/queryFollowers"


@implementation QSNetworkEngine(BrandService)
#pragma mark - Query
- (MKNetworkOperation*)queryBrands:(int)type
                              page:(int)page
                         onSucceed:(ArraySuccessBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_QUERY_BRAND method:@"GET" paramers:@{@"type" : @(type), @"page": @(page)} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* retDict = completedOperation.responseJSON;
        NSArray* retArray = retDict[@"data"][@"brands"];
        if (succeedBlock) {
            succeedBlock(retArray.deepDictMutableCopy, retDict[@"metadata"]);
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
    return [self startOperationWithPath:PATH_QUERY_BRAND_FOLLOWER method:@"GET" paramers:@{@"_id" : brandDict[@"_id"], @"page": @(page)} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* retDict = completedOperation.responseJSON;
        NSArray* retArray = retDict[@"data"][@"peoples"];
        if (succeedBlock) {
            succeedBlock(retArray.deepDictMutableCopy, retDict[@"metadata"]);
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
        } onError:errorBlock];
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
