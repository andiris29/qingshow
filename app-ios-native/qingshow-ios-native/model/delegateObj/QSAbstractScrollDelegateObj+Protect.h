//
//  QSAbstractScrollDelegateObj+Protect.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractScrollDelegateObj.h"

@interface QSAbstractScrollDelegateObj (Protect)

- (MKNetworkOperation*)fetchDataOfPage:(int)page
                      viewRefreshBlock:(VoidBlock)refreshBlock
                            completion:(VoidBlock)block;

@end