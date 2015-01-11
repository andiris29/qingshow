//
//  QSNetworkEngine+PreviewService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/28/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkENgine.h"


@interface QSNetworkEngine(PreviewService)

- (MKNetworkOperation*)getPreviewFeedingPage:(int)page
                                   onSucceed:(ArraySuccessBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)handlePreviewLike:(NSDictionary*)previewDict
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)likePreview:(NSDictionary*)previewDict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)unlikePreview:(NSDictionary*)previewDict
                           onSucceed:(VoidBlock)succeedBlock
                             onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)queryCommentPreview:(NSDictionary*)previewDict
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)addComment:(NSString*)comment
                        onPreview:(NSDictionary*)previewDict
                            reply:(NSDictionary*)peopleDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)deletePreviewComment:(NSDictionary*)commentDict
                                  onSucceed:(VoidBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock;

@end
