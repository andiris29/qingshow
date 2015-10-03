//
//  QSNetworkHelper.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/14.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"
#import "MKNetworkOperation.h"
@interface QSNetworkHelper : NSObject

+ (MKNetworkOperation*)querySystemPathOnSucceed:(VoidBlock)succeedBlock onError:(ErrorBlock)errorBlock;
+ (NSDictionary*)generateHeader;
@end
