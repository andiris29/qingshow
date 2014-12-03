//
//  QSBrandListCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandListCollectionViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSBrandUtil.h"
@implementation QSBrandListCollectionViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)bindWithBrandDict:(NSDictionary*)brandDict
{
    [self.brandImageView setImageFromURL:[QSBrandUtil getBrandLogoUrl:brandDict]];
    [self.infoImageView setImageFromURL:[QSBrandUtil getBrandSloganUrl:brandDict]];
}

@end
