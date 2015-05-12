//
//  QSNetworkOperation.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/9/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "MKNetworkKit.h"
#import "QSError.h"

@interface QSNetworkOperation : MKNetworkOperation
@property (strong, nonatomic) QSError* qsError;
@end
