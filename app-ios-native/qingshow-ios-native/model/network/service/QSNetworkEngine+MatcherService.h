//
//  QSNetworkEngine+MatcherService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/23/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

typedef void (^QueryCategorySucceedBlock)(NSArray* array, NSDictionary* modelCategory, NSDictionary* metadata);

@interface QSNetworkEngine(MatcherService)


- (MKNetworkOperation*)matcherQueryCategoriesOnSucceed:(QueryCategorySucceedBlock)succeedBlock
                                               onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherQueryItemsCategoryId:(NSString*)categoryId
                                              page:(int)page
                                         onSucceed:(ArraySuccessBlock)succeedBlock
                                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherQueryItemsCategory:(NSDictionary*)categoryDict
                                            page:(int)page
                                       onSucceed:(ArraySuccessBlock)succeedBlock
                                         onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherSave:(NSArray*)itemArray
                         itemRects:(NSArray*)itemRects
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherUpdateCover:(UIImage*)cover
                                onSucceed:(DicBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)matcherHide:(NSDictionary*)matcherDict
                         onSucceed:(VoidBlock)succeedBlock
                           onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)matcherRemixByItem:(NSDictionary*)itemDict
                                onSucceed:(DicBlock)succeedBlock
                                  onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)matcherRemixByModel:(NSString*)modelId
                                 onSucceed:(DicBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

@end
