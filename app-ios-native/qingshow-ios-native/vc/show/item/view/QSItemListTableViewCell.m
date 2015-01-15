//
//  QSItemListTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemListTableViewCell.h"
#import "QSItemUtil.h"
#import "QSBrandUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSItemListTableViewCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithItem:(NSDictionary*)itemDict
{
    self.typeLabel.text = [QSItemUtil getItemTypeName:itemDict];
    self.nameLabel.text = [QSItemUtil getItemName:itemDict];
    self.priceLabel.text = [QSItemUtil getPrice:itemDict];
    NSDictionary* brandDict = [QSItemUtil getBrand:itemDict];
    if (brandDict) {
        [self.iconImgView setImageFromURL:[QSBrandUtil getBrandLogoUrl:brandDict]];
    } else {
        self.iconImgView.image = nil;
    }
}

@end
