//
//  QSMatcherItemScrollSelectionCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemScrollSelectionCollectionViewCell.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSImageNameUtil.h"
#define COLOR_GRAY [UIColor colorWithRed:149.f/255.f green:149.f/255.f blue:149.f/255.f alpha:1.f]
#define COLOR_PURPLE [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f]

@implementation QSMatcherItemScrollSelectionCollectionViewCell

- (void)awakeFromNib {
    // Initialization code
    self.imgView.layer.borderWidth = 1.f;
    self.label.hidden = YES;
}

- (void)bindWithDict:(NSDictionary*)dict {
    NSURL *url = [QSImageNameUtil appendImageNameUrl:[QSItemUtil getThumbnail:dict] type:QSImageNameTypeS];
    [self.imgView setImageFromURL:url];
    self.label.text = [QSItemUtil getPriceDesc:dict];
}

- (void)setHover:(BOOL)hover {
    _hover = hover;
    [self _updateColor];
}
- (void)_updateColor {
    if (self.hover) {
        self.imgView.layer.borderColor = COLOR_PURPLE.CGColor;
    } else {
        self.imgView.layer.borderColor = COLOR_GRAY.CGColor;
    }
}
@end
