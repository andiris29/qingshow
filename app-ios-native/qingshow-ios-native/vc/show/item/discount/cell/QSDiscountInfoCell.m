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
#import "UIView+QSExtension.h"

#define COLOR_PURPLE [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f]

@implementation QSDiscountInfoCell

#pragma mark - Life Cycle
- (void)awakeFromNib {
    [super awakeFromNib];
    [self.detailBtn configBorderColor:COLOR_PURPLE width:1.f cornerRadius:DISCOUNT_CELL_CORNER_RADIUS];
    [self.iconImgView configBorderColor:COLOR_PURPLE width:1.f cornerRadius:DISCOUNT_CELL_CORNER_RADIUS];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark - Discount Cell
- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 94.f;
}

- (void)bindWithData:(NSDictionary*)itemDict {
    [self.iconImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.nameLabel.text = [QSItemUtil getItemName:itemDict];
    if ([QSItemUtil getPromoPrice:itemDict]) {
        self.priceLabel.text = [NSString stringWithFormat:@"￥%@",[QSItemUtil getPromoPriceDesc:itemDict]];
    } else {
        self.priceLabel.text = @"";
    }
    [self.priceLabel sizeToFit];
    [self.shopIconImgView setImageFromURL:[QSItemUtil getShopIconUrl:itemDict]];
}

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountInfoCell"];
}

#pragma IBAction
- (IBAction)detailBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(discountCellDetailBtnPressed:)]) {
        [self.delegate discountCellDetailBtnPressed:self];
    }
}
@end
