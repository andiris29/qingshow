//
//  QSDIscountInfoCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountInfoCell.h"
#import "UINib+QSExtension.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSDiscountInfoCell

- (void)awakeFromNib {
    // Initialization code
        [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 72.f;
}

- (void)bindWithData:(NSDictionary*)itemDict {
    [self.iconImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.nameLabel.text = [QSItemUtil getItemName:itemDict];
    if ([QSItemUtil getPromoPrice:itemDict]) {
        self.priceLabel.hidden = NO;
        self.priceNameLabel.hidden = NO;
        self.priceLabel.text = [QSItemUtil getPriceDesc:itemDict];
        self.promPriceLabel.text = [QSItemUtil getPromoPriceDesc:itemDict];
    } else {
        self.priceLabel.hidden = YES;
        self.priceNameLabel.hidden = YES;
        self.promPriceLabel.text = [QSItemUtil getPromoPriceDesc:itemDict];
    }
}

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountInfoCell"];
}
@end
