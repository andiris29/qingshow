//
//  QSNetworkEngine+ChosenService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//
#import "QSNetworkEngine.h"
typedef NS_ENUM(NSUInteger, ChosenType) {
    ChosenTypeHome = 0,
    ChosenTypeFashion = 1
};

@interface QSNetworkEngine(ChosenService)

- (MKNetworkOperation*)chosenFeedByType:(ChosenType)type
                    page:(int)page
               onSucceed:(ArraySuccessBlock)succeedBlock
                 onError:(ErrorBlock)errorBlock;

@end
