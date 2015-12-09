//
//  QSNetworkEngine+ShowService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(ShowService)

#pragma mark - Detail
- (MKNetworkOperation*)queryShowIdDetail:(NSString*)showId
                               onSucceed:(DicBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)queryShowDetail:(NSDictionary*)showDict
                             onSucceed:(DicBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock;
#pragma mark - Comment
- (MKNetworkOperation*)getCommentsOfShowId:(NSString*)showId
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getCommentsOfShow:(NSDictionary*)showDict
                                    page:(int)page
                               onSucceed:(ArraySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)addComment:(NSString*)comment
                         onShowId:(NSString*)showId
                            reply:(NSDictionary*)peopleDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)addComment:(NSString*)comment
                           onShow:(NSDictionary*)showDict
                            reply:(NSDictionary*)peopleDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)deleteCommentId:(NSString*)commentId
                             onSucceed:(VoidBlock)succeedBlock
                               onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)deleteComment:(NSDictionary*)commentDict
                              ofShow:(NSDictionary*)showDict
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock;

#pragma mark - Like
- (MKNetworkOperation*)handleShowLike:(NSDictionary*)showDict
                            onSucceed:(BoolBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)likeShow:(NSDictionary*)showDict
                      onSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)unlikeShow:(NSDictionary*)showDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock;

#pragma mark - Share
- (MKNetworkOperation*)didShareShow:(NSDictionary*)showDict
                          onSucceed:(VoidBlock)succeedBlock
                            onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)getTestShowsOnSucceed:(ArraySuccessBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock;

#pragma mark - View
- (MKNetworkOperation*)viewShow:(NSDictionary*)showDict
                      onSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock;
@end
