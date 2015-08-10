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
                         onSucceed:(StringBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherUuid:(NSString*)uuid
                       updateCover:(UIImage*)cover
                         onSucceed:(DicBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)matcherHide:(NSDictionary*)matcherDict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

@end
