//
//  QSSectionImageTextButton.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSectionFollowButton.h"


@implementation QSSectionFollowButton
+ (QSSectionFollowButton*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSSectionFollowButton" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    return array[0];
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}
- (void)setFollowed:(BOOL)fIsFollowed
{
    if (fIsFollowed) {
        self.iconImageView.image = [UIImage imageNamed:@"badge_unfollow_btn"];
    } else {
        self.iconImageView.image = [UIImage imageNamed:@"badge_follow_btn"];
    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
