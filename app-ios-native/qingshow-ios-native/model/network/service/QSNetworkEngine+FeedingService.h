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

- (MKNetworkOperation*)getChosenFeedingType:(int)type
                                       page:(int)page
                                  onSucceed:(ArraySuccessBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)getLikeFeedingUser:(NSDictionary*)userDict
                                     page:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getRecommendationFeedingPage:(int)page
                                          onSucceed:(ArraySuccessBlock)succeedBlock
                                            onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)getRecommendationFeedingDate:(NSDate*)date
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
- (MKNetworkOperation*)feedingByTopic:(NSDictionary*)topicDic
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation *)getHotFeedingPage:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)feedingMatchCreateBy:(NSDictionary*)peopleDict
                                       page:(int)page
                                  onSucceed:(ArraySuccessBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock;
#pragma mark - add matchHot/matchNew
- (MKNetworkOperation *)getfeedingMatchTimeFromDate:(NSDate*)fromDate
                                                 toDate:(NSDate*)toDate
                                                   page:(int)page
                                              onSucceed:(ArraySuccessBlock)succeedBlock
                                                onError:(ErrorBlock)errorBlock;
@end
