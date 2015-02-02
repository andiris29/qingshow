//
//  QSNetworkEngine+BrandService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(BrandService)

- (MKNetworkOperation*)queryBrands:(NSNumber*)type
                              page:(int)page
                         onSucceed:(ArraySuccessBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)queryBrandsDetail:(NSDictionary*)brandDict
                               onSucceed:(DicBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)handleFollowBrand:(NSDictionary*)brandDict
                               onSucceed:(BoolBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)queryBrandFollower:(NSDictionary*)brandDict
                                     page:(int)page
                                onSucceed:(ArraySuccessBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
@end