//
//  QSNetworkEngine+FeedingService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+FeedingService.h"
#import "NSArray+QSExtension.h"
#import "QSNetworkEngine+Protect.h"

//Path
#define PATH_FEEDING_CHOSEN @"feeding/chosen"
#define PATH_FEEDING_BY_MODEL @"feeding/byModel"
#define PATH_FEEDING_HOT @"feeding/hot"
#define PATH_FEEDING_BY_TAGS @"feeding/byTags"
#define PATH_FEEDING_STUDIO @"feeding/studio"
#define PATH_FEEDING_LIKE @"feeding/like"
#define PATH_FEEDING_RECOMMENDATION @"feeding/recommendation"
#define PATH_FEEDING_BY_BRAND @"feeding/byBrand"
#define PATH_FEEDING_BY_BRAND_DISCOUNT @"feeding/byBrandDiscount"



@interface QSNetworkEngine (Private)

- (MKNetworkOperation*)getFeedingPath:(NSString*)path
                           otherParam:(NSDictionary*)paramDict
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;

@end

@implementation QSNetworkEngine(FeedingService)

#pragma mark - Private 
- (MKNetworkOperation*)getFeedingPath:(NSString*)path
                           otherParam:(NSDictionary*)paramDict
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    NSMutableDictionary* param = [paramDict mutableCopy];
    if (!param) {
        param = [@{} mutableCopy];
    }
    param[@"pageNo"] = @(page);
    param[@"pageSize"] = @10;
    
    return [self startOperationWithPath:path
                                 method:@"GET"
                               paramers:param
                            onSucceeded:^(MKNetworkOperation *completedOperation)
            {
                NSDictionary* retDict = completedOperation.responseJSON;
                if (succeedBlock) {
                    NSArray* shows = retDict[@"data"][@"shows"];
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

#pragma mark - Feeding
- (MKNetworkOperation*)getChosenFeedingPage:(int)page
                                  onSucceed:(ArraySuccessBlock)succeedBlock
                                    onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_CHOSEN otherParam:nil page:page onSucceed:succeedBlock onError:errorBlock];
}
- (MKNetworkOperation*)getLikeFeedingPage:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_LIKE otherParam:nil page:page onSucceed:succeedBlock onError:errorBlock];
}
- (MKNetworkOperation*)getRecommendationFeedingPage:(int)page
                                          onSucceed:(ArraySuccessBlock)succeedBlock
                                            onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_RECOMMENDATION otherParam:nil page:page onSucceed:succeedBlock onError:errorBlock];
}


- (MKNetworkOperation*)getCategoryFeeding:(int)type
                                     page:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock
{
    NSString* path = nil;
    switch (type) {
        case 1:
            path = PATH_FEEDING_CHOSEN;
            break;
        case 2:
            path = PATH_FEEDING_HOT;
            break;
        case 8:
            path = PATH_FEEDING_STUDIO;
            break;
        default:
            break;
    }
    return [self getFeedingPath:path otherParam:nil page:page onSucceed:succeedBlock onError:errorBlock];
}


- (MKNetworkOperation*)getFeedByModel:(NSString*)modelId
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_BY_MODEL otherParam:@{@"_id" : modelId} page:page onSucceed:succeedBlock onError:errorBlock];
}
- (MKNetworkOperation*)feedingByBrand:(NSDictionary*)brandDict
                                 page:(int)page
                            onSucceed:(ArraySuccessBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_BY_BRAND otherParam:@{@"_id" : brandDict[@"_id"]} page:page onSucceed:succeedBlock onError:errorBlock];
}

- (MKNetworkOperation*)feedingByBrandDiscount:(NSDictionary*)brandDict
                                         page:(int)page
                                    onSucceed:(ArraySuccessBlock)succeedBlock
                                      onError:(ErrorBlock)errorBlock
{
    return [self getFeedingPath:PATH_FEEDING_BY_BRAND_DISCOUNT otherParam:@{@"_id" : brandDict[@"_id"]} page:page onSucceed:succeedBlock onError:errorBlock];
}

@end
