//
//  QSRemixImageView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/1.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSRemixImageView.h"
#import "UIView+QSExtension.h"
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
    self.isSelected = NO;
}
#pragma mark - Getter And Setter
- (void)setIsSelected:(BOOL)isSelected {
    _isSelected = isSelected;
    [self _updateForIsSelect];
}

- (void)_updateForIsSelect {
    if (_isSelected) {
        [self configBorderColor:[UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f] width:1 cornerRadius:0];
    } else {
        [self configBorderColor:[UIColor clearColor] width:0 cornerRadius:0];
    }
}
@end
