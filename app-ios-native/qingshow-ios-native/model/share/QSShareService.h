//
//  QSShareService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 8/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"

#define SHARE_SHARE_SERVICE [QSShareService shareService]

@interface QSShareService : NSObject

+ (void)configShareHost:(NSString*)shareHost;
+ (NSString*)getShareHost;

+ (QSShareService*)shareService;

- (void)shareWithWechatMoment:(NSString*)title
                         desc:(NSString*)desc
                    imagePath:(NSString*)imagePath
                          url:(NSString*)url
                    onSucceed:(VoidBlock)succeedBlock
                      onError:(ErrorBlock)errorBlock;
- (void)shareWithWechatFriend:(NSString*)title
                         desc:(NSString*)desc
                    imagePath:(NSString*)imagePath
                          url:(NSString*)url
                    onSucceed:(VoidBlock)succeedBlock
                      onError:(ErrorBlock)errorBlock;

//朋友圈
- (void)shareWithWechatMoment:(NSString*)title
                         desc:(NSString*)desc
                        image:(UIImage*)image
                          url:(NSString*)url
                    onSucceed:(VoidBlock)succeedBlock
                      onError:(ErrorBlock)errorBlock;
- (void)shareWithWechatFriend:(NSString*)title
                         desc:(NSString*)desc
                        image:(UIImage*)image
                          url:(NSString*)url
                    onSucceed:(VoidBlock)succeedBlock
                      onError:(ErrorBlock)errorBlock;

@end
