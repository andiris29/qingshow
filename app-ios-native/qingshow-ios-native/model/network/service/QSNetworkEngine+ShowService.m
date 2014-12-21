//
//  QSNetworkEngine+ShowService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+ShowService.h"
#import "QSNetworkEngine+Protect.h"
#import "QSShowUtil.h"

//Query
#define PATH_QUERY_SHOW @"show/query"
//Comment
#define PATH_SHOW_QUERY_COMMENTS @"show/queryComments"
#define PATH_SHOW_COMMENT @"show/comment"
#define PATH_SHOW_DELETE_COMMENT @"show/deleteComment"
//Like
#define PATH_SHOW_LIKE @"show/like"
#define PATH_SHOW_UNLIKE @"show/unlike"

@implementation QSNetworkEngine(ShowService)
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

#pragma mark - Comment
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
    return [self startOperationWithPath:PATH_SHOW_DELETE_COMMENT method:@"POST" paramers:@{@"_id" : commentDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

#pragma mark - Like
- (MKNetworkOperation*)likeShow:(NSDictionary*)showDict
                      onSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHOW_LIKE method:@"POST" paramers:@{@"_id" : showDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        [QSShowUtil setIsLike:YES show:showDict];
        [QSShowUtil addNumberLike:1ll forShow:showDict];
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
        [QSShowUtil addNumberLike:-1ll forShow:showDict];
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
