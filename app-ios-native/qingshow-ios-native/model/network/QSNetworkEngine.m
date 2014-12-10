//
//  QSNetworkEngine.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine.h"
#import "ServerPath.h"
#import "QSNetworkOperation.h"
#import "QSUserManager.h"
#import "QSFeedingCategory.h"
#import "NSArray+QSExtension.h"
#import "QSBrandUtil.h"
#import "QSPeopleUtil.h"
#import "QSShowUtil.h"

//Query
#define PATH_QUERY_COMMENT @"query/comments"
#define PATH_QUERY_SHOW @"show/query"
//Show
#define PATH_SHOW_QUERY_COMMENTS @"show/queryComments"
#define PATH_SHOW_COMMENT @"show/comment"

// People
#define PATH_PEOPLE_QUERY_MODELS @"people/queryModels"
#define PATH_PEOPLE_FOLLOW @"people/follow"
#define PATH_PEOPLE_UNFOLLOW @"people/unfollow"
#define PATH_PEOPLE_QUERY_FOLLOWER @"people/queryFollowers"
#define PATH_PEOPLE_QUERY_FOLLOWED @"people/queryFollowed"

//Interaction
#define PATH_PEOPLE_FOLLOW_BRAND @"brand/follow"
#define PATH_PEOPLE_UNFOLLOW_BRAND @"brand/unfollow"
#define PATH_SHOW_LIKE @"show/like"
#define PATH_SHOW_UNLIKE @"show/unlike"

@interface QSNetworkEngine (Protect)
- (MKNetworkOperation*)startOperationWithPath:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock;
- (MKNetworkOperation *)startOperationWithPath:(NSString *)path
                                        method:(NSString *)method
                                      paramers:(NSDictionary *)paramDict
                                       fileKey:(NSString *)fileKey
                                         image:(NSData *)image
                                   onSucceeded:(OperationSucceedBlock)succeedBlock
                                       onError:(OperationErrorBlock)errorBlock;
@end


@implementation QSNetworkEngine

#pragma mark - Static Method
+ (QSNetworkEngine*)shareNetworkEngine
{
    static QSNetworkEngine* s_networkEngine = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_networkEngine = [[QSNetworkEngine alloc] initWithHostName:HOST_NAME];
        [s_networkEngine registerOperationSubclass:[QSNetworkOperation class]];
        [NSHTTPCookieStorage sharedHTTPCookieStorage].cookieAcceptPolicy = NSHTTPCookieAcceptPolicyAlways;
        
    });
    return s_networkEngine;
}

#pragma mark - Basic
- (MKNetworkOperation*)startOperationWithPath:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    NSMutableDictionary* p = [paramDict mutableCopy];
    p[@"version"] = @"1.0.0";
    op = [self operationWithPath:path params:p httpMethod:method ];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}

- (MKNetworkOperation *)startOperationWithPath:(NSString *)path
                                        method:(NSString *)method
                                      paramers:(NSDictionary *)paramDict
                                       fileKey:(NSString *)fileKey
                                         image:(NSData *)image
                                    onSucceeded:(OperationSucceedBlock)succeedBlock
                                       onError:(OperationErrorBlock)errorBlock {
    NSMutableDictionary* p = [paramDict mutableCopy];
    p[@"version"] = @"1.0.0";
    MKNetworkOperation *op = nil;
    op = [self operationWithPath:path params:p httpMethod:method];
    [op addData:image forKey:fileKey];
//    [op setFreezable:YES];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}



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

#pragma mark - Interaction
- (MKNetworkOperation*)handleFollowModel:(NSDictionary*)model
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    NSString* modelId = model[@"_id"];
    if ([QSPeopleUtil getPeopleIsFollowed:model]) {
        return [self unfollowPeople:modelId onSucceed:^{
            [QSPeopleUtil setPeople:model isFollowed:NO];
            
            if (succeedBlock) {
                succeedBlock(NO);
            }
        } onError:errorBlock];
    }
    else
    {
        return [self followPeople:modelId onSucceed:^{
            [QSPeopleUtil setPeople:model isFollowed:YES];
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


- (MKNetworkOperation*)addComment:(NSString*)comment
                           onShow:(NSDictionary*)showDict
                            reply:(NSDictionary*)peopleDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{@"_id": showDict[@"_id"], @"comment": comment} mutableCopy];
    if (peopleDict) {
        paramDict[@"_atId"] = peopleDict[@"_id"];
    }
    
    return [self startOperationWithPath:PATH_SHOW_COMMENT method:@"POST" paramers:paramDict onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)deleteComment:(NSDictionary*)commentDict
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:@"show/deleteComment" method:@"POST" paramers:@{@"_id" : commentDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}



#pragma mark - Query
- (MKNetworkOperation*)queryShowDetail:(NSDictionary*)showDict
                             onSucceed:(DicBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_QUERY_SHOW method:@"GET" paramers:@{@"_ids" : showDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            if ([completedOperation.responseJSON isKindOfClass:[NSDictionary class]])
            {
                NSDictionary *retDict = completedOperation.responseJSON;
                NSArray* dataArray = retDict[@"data"][@"shows"];
                NSDictionary* d = nil;
                if (dataArray.count) {
                    d = dataArray[0];
                }
                succeedBlock([d mutableCopy]);
                return;
            } else if ([completedOperation.responseJSON isKindOfClass:[NSArray class]]) {
                NSArray* retArray = completedOperation.responseJSON;
                if (retArray.count) {
                    succeedBlock(retArray[0]);
                    return;
                }
            }
            succeedBlock(nil);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)getCommentsOfShow:(NSDictionary*)showDict
                                    page:(int)page
                               onSucceed:(ArraySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHOW_QUERY_COMMENTS
                                 method:@"GET"
                               paramers:@{
                                          @"_id": showDict[@"_id"],
                                          @"pageNo" : @(page),
                                          @"pageSize" : @10
                                          }
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary *retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    succeedBlock(retDict[@"data"][@"showComments"], retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
                
            }];
}



- (MKNetworkOperation*)likeShow:(NSDictionary*)showDict
                      onSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHOW_LIKE method:@"POST" paramers:@{@"_id" : showDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [QSShowUtil setIsLike:YES show:showDict];
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}
- (MKNetworkOperation*)unlikeShow:(NSDictionary*)showDict
                      onSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHOW_UNLIKE method:@"POST" paramers:@{@"_id" : showDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [QSShowUtil setIsLike:NO show:showDict];
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

#pragma mark - Brand
- (MKNetworkOperation*)queryBrands:(int)type
                              page:(int)page
                         onSucceed:(ArraySuccessBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:@"brand/queryBrands" method:@"GET" paramers:@{@"type" : @(type), @"page": @(page)} onSucceeded:^(MKNetworkOperation *completedOperation) {
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
    return [self startOperationWithPath:@"brand/queryBrands" method:@"GET" paramers:@{@"_id" : brandDict[@"_id"], @"page": @(page)} onSucceeded:^(MKNetworkOperation *completedOperation) {
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
    return [self startOperationWithPath:@"brand/follow"
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
    return [self startOperationWithPath:@"brand/unfollow"
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
