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
#warning label
    [self.imageView setImageFromURL:[QSShowUtil getCoverUrl:showDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:NO];
    
    self.label2.text = @"";
}

- (void)bindWithItem:(NSDictionary*)itemDict
{

}
@end
