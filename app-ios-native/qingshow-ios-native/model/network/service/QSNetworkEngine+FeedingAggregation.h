//
//  QSNetworkEngine+FeedingAggregation.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/23.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine.h"

@interface QSNetworkEngine(FeedingAggregation)

- (MKNetworkOperation*)feedingAggregationOnSucceed:(ArraySuccessBlock)succeedBlock
                                           onError:(ErrorBlock)errorBlock;

@end
