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
#import "NSMutableDictionary+QSExtension.h"
#import "QSError.h"
#import "QSCommonUtil.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"

//Query
#define PATH_QUERY_SHOW @"show/query"
//Comment
#define PATH_SHOW_QUERY_COMMENTS @"show/queryComments"
#define PATH_SHOW_COMMENT @"show/comment"
#define PATH_SHOW_DELETE_COMMENT @"show/deleteComment"
//Like
#define PATH_SHOW_LIKE @"show/like"
#define PATH_SHOW_UNLIKE @"show/unlike"
//Share
#define PATH_SHOW_SHARE @"show/share"


@implementation QSNetworkEngine(ShowService)
#pragma mark - Query
- (MKNetworkOperation*)getTestShowsOnSucceed:(ArraySuccessBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_QUERY_SHOW method:@"GET" paramers:@{@"_ids" : @"555aa9d538dadbed5a997eed"} onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary *retDict = completedOperation.responseJSON;
        NSArray* dataArray = retDict[@"data"][@"shows"];
        
        succeedBlock([dataArray deepMutableCopy], retDict[@"metadata"]);
        
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)queryShowDetail:(NSDictionary*)showDict
                             onSucceed:(DicBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock
{

    return [self startOperationWithPath:PATH_QUERY_SHOW method:@"GET" paramers:@{@"_ids" : [QSCommonUtil getIdOrEmptyStr:showDict]} onSucceeded:^(MKNetworkOperation *completedOperation) {
      
        if (succeedBlock) {
            if ([completedOperation.responseJSON isKindOfClass:[NSDictionary class]])
            {
                NSDictionary *retDict = completedOperation.responseJSON;
                NSArray* dataArray = retDict[@"data"][@"shows"];
                NSDictionary* d = nil;
                if (dataArray.count) {
                    d = dataArray[0];
                }
                if ([showDict isKindOfClass:[NSMutableDictionary class]]) {
                    NSMutableDictionary* mD = (NSMutableDictionary*)showDict;
                    [mD updateWithDict:d];
                }
                succeedBlock(showDict);
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
        [QSShowUtil addNumberComment:1ll forShow:showDict];
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
                              ofShow:(NSDictionary*)showDict
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHOW_DELETE_COMMENT method:@"POST" paramers:@{@"_id" : commentDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (showDict) {
            [QSShowUtil addNumberComment:-1ll forShow:showDict];
        }
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
- (MKNetworkOperation*)handleShowLike:(NSDictionary*)showDict
                            onSucceed:(BoolBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    if ([QSShowUtil getIsLike:showDict]) {
        op = [self unlikeShow:showDict onSucceed:^{
            succeedBlock(NO);
        } onError:errorBlock];
    } else {
        op = [self likeShow:showDict onSucceed:^{
            succeedBlock(YES);
        } onError:errorBlock];
    }
    return op;
}

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
        if ([error isKindOfClass:[QSError class]]) {
            if (error.code == kQSErrorCodeAlreadyFollow) {
                [QSShowUtil setIsLike:YES show:showDict];
            } else if (error.code == kQSErrorCodeAlreadyUnfollow) {
                [QSShowUtil setIsLike:NO show:showDict];
            }
        }
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
        if ([error isKindOfClass:[QSError class]]) {
            if (error.code == kQSErrorCodeAlreadyFollow) {
                [QSShowUtil setIsLike:YES show:showDict];
            } else if (error.code == kQSErrorCodeAlreadyUnfollow) {
                [QSShowUtil setIsLike:NO show:showDict];
            }
        }
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

#pragma mark - Share
- (MKNetworkOperation*)didShareShow:(NSDictionary*)showDict
                          onSucceed:(VoidBlock)succeedBlock
                            onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHOW_SHARE method:@"POST" paramers:@{@"_id" : showDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
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
