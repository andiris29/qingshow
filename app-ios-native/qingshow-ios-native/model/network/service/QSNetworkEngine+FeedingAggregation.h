//
//  QSNetworkEngine+FeedingAggregation.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/23.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine.h"

@interface QSNetworkEngine(FeedingAggregation)

- (MKNetworkOperation*)aggregationFeaturedTopOwners:(NSDate*)date onSucceed:(TopOwnerBlock)succeedBlock onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)aggregationMatchHotTopOwners:(NSDate*)date onSucceed:(TopOwnerBlock)succeedBlock onError:(ErrorBlock)errorBlock;
#warning TODO
- (MKNetworkOperation*)aggregationMatchNew:(NSDate*)date onSucceed:(TopOwnerAndShowBlock)succeedBlock onError:(ErrorBlock)errorBlock;
@end
