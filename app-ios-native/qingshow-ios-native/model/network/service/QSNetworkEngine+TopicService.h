//
//  QSNetworkEngine+TopicService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine+TopicService.h"

@interface QSNetworkEngine(TopicService)

- (MKNetworkOperation*)queryTopicPage:(int)page
                            OnSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;

@end
