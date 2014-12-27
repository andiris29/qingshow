//
//  QSSectionImageTextButton.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSectionButtonBase.h"

@interface QSSectionFollowButton : QSSectionButtonBase

@property (strong, nonatomic) IBOutlet UIImageView* iconImageView;

+ (QSSectionFollowButton*)generateView;

- (void)setFollowed:(BOOL)fIsFollowed;

@end
