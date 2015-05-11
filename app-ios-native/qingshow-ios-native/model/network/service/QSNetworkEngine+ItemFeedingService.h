//
//  QSNetworkEngine+ItemFeedingService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(ItemFeedingService)
- (MKNetworkOperation*)getItemFeedingLikePage:(int)page
                                    onSucceed:(ArraySuccessBlock)successBlock
                                      onError:(ErrorBlock)errorBlock;
@end
