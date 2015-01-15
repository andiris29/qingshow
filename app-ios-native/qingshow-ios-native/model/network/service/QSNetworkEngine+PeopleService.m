//
//  QSNetworkEngine+PeopleService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+Protect.h"
#import "QSNetworkEngine+PeopleService.h"
#import "NSArray+QSExtension.h"
#import "QSPeopleUtil.h"

// People
#define PATH_PEOPLE_QUERY_MODELS @"people/queryModels"
#define PATH_PEOPLE_FOLLOW @"people/follow"
#define PATH_PEOPLE_UNFOLLOW @"people/unfollow"
#define PATH_PEOPLE_QUERY_FOLLOWER @"people/queryFollowers"
#define PATH_PEOPLE_QUERY_FOLLOWED @"people/queryFollowed"
#define PATH_PEOPLE_QUERY_FOLLOWED_BRAND @"brand/queryFollowedBrands"


@implementation QSNetworkEngine(PeopleService)

#pragma mark - Model
- (MKNetworkOperation*)getModelListPage:(int)page
                              onSucceed:(ArraySuccessBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PEOPLE_QUERY_MODELS
                                 method:@"GET"
                               paramers:@{@"pageNo" : @(page),
                                          @"paegSize" : @10}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    NSArray* peopleList = retDict[@"data"][@"peoples"];
                    
                    succeedBlock([peopleList deepDictMutableCopy], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}
#pragma mark - Follow
- (MKNetworkOperation*)peopleQueryFollowed:(NSDictionary*)peopleDict
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock

{
    if (!peopleDict) {
        errorBlock(nil);
        return nil;
    }
    return [self startOperationWithPath:PATH_PEOPLE_QUERY_FOLLOWED
                                 method:@"GET"
                               paramers:@{@"_id" : peopleDict[@"_id"], @"pageNo" : @(page),@"paegSize" : @10}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* responseDict = completedOperation.responseJSON;
                NSArray* a = responseDict[@"data"][@"peoples"];
                if (succeedBlock) {
                    succeedBlock([a deepDictMutableCopy], responseDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}
- (MKNetworkOperation*)peopleQueryFollowedBrand:(NSDictionary*)peopleDict
                                           page:(int)page
                                      onSucceed:(ArraySuccessBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock

{
    if (!peopleDict) {
        errorBlock(nil);
        return nil;
    }
    return [self startOperationWithPath:PATH_PEOPLE_QUERY_FOLLOWED_BRAND
                                 method:@"GET"
                               paramers:@{@"_id" : peopleDict[@"_id"], @"pageNo" : @(page),@"paegSize" : @10}
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* responseDict = completedOperation.responseJSON;
                NSArray* a = responseDict[@"data"][@"brands"];
                if (succeedBlock) {
                    succeedBlock([a deepDictMutableCopy], responseDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}
- (MKNetworkOperation*)peopleQueryFollower:(NSDictionary*)peopleDict
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PEOPLE_QUERY_FOLLOWER method:@"GET" paramers:@{@"_id" : peopleDict[@"_id"], @"pageNo" : @(page),@"paegSize" : @10} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* responseDict = completedOperation.responseJSON;
        NSArray* a = responseDict[@"data"][@"peoples"];
        if (succeedBlock) {
            succeedBlock([a deepDictMutableCopy], responseDict[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

#pragma mark - Interaction Follow
- (MKNetworkOperation*)handleFollowModel:(NSDictionary*)model
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    NSString* modelId = model[@"_id"];
    if ([QSPeopleUtil getPeopleIsFollowed:model]) {
        return [self unfollowPeople:modelId onSucceed:^{
            [QSPeopleUtil setPeople:model isFollowed:NO];
            [QSPeopleUtil addNumFollower:-1ll forPeople:model];
            if (succeedBlock) {
                succeedBlock(NO);
            }
        } onError:errorBlock];
    }
    else
    {
        return [self followPeople:modelId onSucceed:^{
            [QSPeopleUtil setPeople:model isFollowed:YES];
            [QSPeopleUtil addNumFollower:1ll forPeople:model];
            if (succeedBlock) {
                succeedBlock(YES);
            }
        } onError:errorBlock];
    }
}
- (MKNetworkOperation*)followPeople:(NSString*)peopleId
                          onSucceed:(VoidBlock)succeedBlock
                            onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PEOPLE_FOLLOW
                                 method:@"POST" paramers:@{@"_id" : peopleId}
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

- (MKNetworkOperation*)unfollowPeople:(NSString*)peopleId
                            onSucceed:(VoidBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PEOPLE_UNFOLLOW
                                 method:@"POST"
                               paramers:@{@"_id" : peopleId}
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
