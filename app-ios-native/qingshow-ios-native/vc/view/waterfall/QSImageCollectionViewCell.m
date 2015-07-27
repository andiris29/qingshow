//
//  QSImageCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSImageCollectionViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSShowUtil.h"
#import "QSItemUtil.h"

@implementation QSImageCollectionViewCell

- (void)awakeFromNib
{
    if (self.discountLabel) {
        self.discountLabel.layer.cornerRadius = self.discountLabel.frame.size.height / 2;
        self.discountLabel.layer.masksToBounds = YES;
    }
    
}

- (void)bindWithShow:(NSDictionary*)showDict
{
    [self.imageView setImageFromURL:[QSShowUtil getCoverUrl:showDict]];
    self.label1.text = @"";
    self.label2.text = @"";
}

- (void)bindWithItem:(NSDictionary*)itemDict
{
    [self.imageView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict]];
    self.label1.text = [QSItemUtil getItemName:itemDict];
    self.label2.text = [QSItemUtil getPriceAfterDiscount:itemDict];
}
@end
