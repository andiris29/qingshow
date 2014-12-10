//
//  QSNetworkEngine+FeedingService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(FeedingService)

- (MKNetworkOperation*)getChosenFeedingPage:(int)page
                                  onSucceed:(ArraySuccessBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getLikeFeedingPage:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getRecommendationFeedingPage:(int)page
                                          onSucceed:(ArraySuccessBlock)succeedBlock
                                            onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getCategoryFeeding:(int)type
                                     page:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getFeedByModel:(NSString*)modelId
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)feedingByBrand:(NSDictionary*)brandDict
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)feedingByBrandDiscount:(NSDictionary*)brandDict
                                         page:(int)page
                                    onSucceed:(ArraySuccessBlock)succeedBlock
                                      onError:(ErrorBlock)errorBlock;

@end
