//
//  QSMatcherItemImageView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemPageSelectionImageView.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSItemUtil.h"
#import "UINib+QSExtension.h"
#define COLOR_GRAY [UIColor colorWithRed:149.f/255.f green:149.f/255.f blue:149.f/255.f alpha:1.f]
#define COLOR_PINK [UIColor colorWithRed:240.f/255.f green:149.f/255.f blue:164.f/255.f alpha:1.f]

@implementation QSMatcherItemPageSelectionImageView


+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherItemPageSelectionImageView"];
}

- (void)awakeFromNib {
    self.imgView.layer.borderWidth = 1.f;
    self.hovered = NO;
    [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTap:)]];
}

- (void)setHovered:(BOOL)hovered {
    _hovered = hovered;
    [self updateColor];
}

- (void)updateColor {
    if (_hovered) {
        self.imgView.layer.borderColor = COLOR_PINK.CGColor;
    } else {
        self.imgView.layer.borderColor = COLOR_GRAY.CGColor;
    }
}

- (void)bindWithItem:(NSDictionary*)itemDict {
    [self.imgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.priceLabel.text = [QSItemUtil getPriceDesc:itemDict];
}

- (void)didTap:(UITapGestureRecognizer*)ges {
    [self sendActionsForControlEvents:UIControlEventTouchUpInside];
}

@end
