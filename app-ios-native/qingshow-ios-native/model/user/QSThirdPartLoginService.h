//
//  QSThirdPartLoginService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/12/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"
@interface QSThirdPartLoginService : NSObject

+ (QSThirdPartLoginService*)getInstance;

- (void)loginWithWechatOnSuccees:(VoidBlock)succeedBlock
                         onError:(ErrorBlock)errorBlock;
- (void)bindWithWechatOnSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock;
@end
