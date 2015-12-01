//
//  QSRemixImageView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/1.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSRemixImageView.h"

@interface QSRemixImageView ()


@property (strong, nonatomic) UIImageView* selectedIndicator;

@end
@implementation QSRemixImageView

#pragma mark - Init
- (instancetype)init {
    self = [super init];
    if (self) {
        [self _initConfig];
    }
    return self;
}
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self _initConfig];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)layoutSubviews {
    [super layoutSubviews];
    self.imageView.frame = self.bounds;
    self.selectedIndicator.frame = self.bounds;
}

#pragma mark - Private
- (void)_initConfig {
    self.imageView = [[UIImageView alloc] init];
    [self addSubview:self.imageView];
    self.imageView.contentMode = UIViewContentModeScaleAspectFill;
    self.selectedIndicator = [[UIImageView alloc] init];
    [self addSubview:self.selectedIndicator];
}
@end
