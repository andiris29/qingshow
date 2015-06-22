//
//  QSNetworkEngine+MatcherService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/23/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(MatcherService)

- (MKNetworkOperation*)matcherQueryCategoriesOnSucceed:(ArraySuccessBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherQueryItemsCategory:(NSDictionary*)categoryDict
                                            page:(int)page
                                       onSucceed:(ArraySuccessBlock)succeedBlock
                                         onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherSave:(NSArray*)itemArray
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcher:(NSDictionary*)matcherDict
                   updateCover:(UIImage*)cover
                     onSucceed:(DicBlock)succeedBlock
                       onError:(ErrorBlock)errorBlock;

@end
