//
//  QSNetworkEngine+PreviewService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/28/14.
//  Copyright (c) 2014 QS. All rights reserved.
//


#import "QSNetworkEngine+PreviewService.h"
#import "QSNetworkEngine+Protect.h"
#import "NSArray+QSExtension.h"
#import "QSPreviewUtil.h"

#define PATH_PREVIEW_FEED @"preview/feed"
#define PATH_PREVIEW_LIKE @"preview/like"
#define PATH_PREVIEW_UNLIKE @"preview/unlike"
#define PATH_PREVIEW_QUERY_COMMENTS @"preview/queryComments"
#define PATH_PREVIEW_COMMENT @"preview/comment"
#define PATH_PREVIEW_DELETE_COMMENT @"preview/deleteCOmment"

@implementation QSNetworkEngine(PreviewService)

- (MKNetworkOperation*)getPreviewFeedingPage:(int)page
                                   onSucceed:(ArraySuccessBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* param = [@{} mutableCopy];
    if (!param) {
        param = [@{} mutableCopy];
    }
    param[@"pageNo"] = @(page);
    param[@"pageSize"] = @10;
    
    return [self startOperationWithPath:PATH_PREVIEW_FEED
                                 method:@"GET"
                               paramers:param
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    NSArray* shows = retDict[@"data"][@"previews"];
                    succeedBlock(shows.deepDictMutableCopy, retDict[@"metadata"]);
                }
            }
                                onError:^(MKNetworkOperation *completedOperation, NSError *error)
            {
                if (errorBlock) {
                    errorBlock(error);
                }
            }];
}

- (MKNetworkOperation*)handlePreviewLike:(NSDictionary*)previewDict
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    if ([QSPreviewUtil getIsLike:previewDict]) {
        op = [self unlikePreview:previewDict onSucceed:^{
            [QSPreviewUtil setIsLike:NO preview:previewDict];
            [QSPreviewUtil addNumberLike:-1ll forShow:previewDict];
            succeedBlock(NO);
        } onError:errorBlock];
    } else {
        op = [self likePreview:previewDict onSucceed:^{
            [QSPreviewUtil setIsLike:YES preview:previewDict];
            [QSPreviewUtil addNumberLike:1ll forShow:previewDict];
            succeedBlock(YES);
        } onError:errorBlock];
    }
    return op;
}


- (MKNetworkOperation*)likePreview:(NSDictionary*)previewDict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PREVIEW_LIKE method:@"POST" paramers:@{@"_id" : previewDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)unlikePreview:(NSDictionary*)previewDict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PREVIEW_UNLIKE method:@"POST" paramers:@{@"_id" : previewDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)queryCommentPreview:(NSDictionary*)previewDict
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PREVIEW_QUERY_COMMENTS method:@"POST" paramers:@{@"_id" : previewDict[@"_id"], @"pageNo": @(page), @"pageSize" : @10 } onSucceeded:^(MKNetworkOperation *completedOperation) {
        NSDictionary* retDict = completedOperation.responseJSON;
        if (succeedBlock) {
            succeedBlock(retDict[@"data"][@"previewComments"], retDict[@"metadata"]);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}


- (MKNetworkOperation*)addComment:(NSString*)comment
                        onPreview:(NSDictionary*)previewDict
                            reply:(NSDictionary*)peopleDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* paramDict = [@{@"_id": previewDict[@"_id"], @"comment": comment} mutableCopy];
    if (peopleDict) {
        paramDict[@"_atId"] = peopleDict[@"_id"];
    }
    
    return [self startOperationWithPath:PATH_PREVIEW_COMMENT method:@"POST" paramers:paramDict onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            succeedBlock();
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
}

- (MKNetworkOperation*)deletePreviewComment:(NSDictionary*)commentDict
                                  onSucceed:(VoidBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_PREVIEW_DELETE_COMMENT method:@"POST" paramers:@{@"_id" : commentDict[@"_id"]} onSucceeded:^(MKNetworkOperation *completedOperation) {
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