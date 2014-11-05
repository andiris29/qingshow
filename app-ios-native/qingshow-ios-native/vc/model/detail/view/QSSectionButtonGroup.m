//
//  QSSectionButtonGroup.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSectionButtonGroup.h"
#import "QSSectionNumberTextButton.h"
#import "QSSectionImageTextButton.h"
@interface QSSectionButtonGroup ()

@property (strong, nonatomic) NSArray* buttonGroup;

@property (strong, nonatomic) QSSectionImageTextButton* followButton;

@end

@implementation QSSectionButtonGroup

- (id)init
{
    self = [self initWithFrame:CGRectMake(0, 0, 320, 45)];
    if (self) {
        self.buttonGroup = @[
                             [QSSectionNumberTextButton generateView],
                             [QSSectionNumberTextButton generateView],
                             [QSSectionNumberTextButton generateView]
                             ];
        self.followButton = [QSSectionImageTextButton generateView];
        for (QSSectionButtonBase* btn in self.buttonGroup){
            [self addSubview:btn];
        }
        [self addSubview:self.followButton];
        [self updateLayout];
    }
    return self;
}
- (void)layoutSubviews
{
    [super layoutSubviews];
    [self updateLayout];
}
- (void)updateLayout
{
    CGSize size = self.frame.size;
    float width = size.width / (self.buttonGroup.count + 1);
    float height = size.height;
    for (int i = 0; i < self.buttonGroup.count; i++) {
        QSSectionButtonBase* btn = self.buttonGroup[i];
        btn.frame = CGRectMake(i * width, 0, width - 1, height);
    }
    self.followButton.frame = CGRectMake(self.buttonGroup.count * width, 0, width - 1, height);
}

@end
