//
//  QS11OrderInfoCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS12TradeInfoCell.h"
#import "UINib+QSExtension.h"
#import "QSTradeUtil.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSS12TradeInfoCell
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSS12TradeInfoCell"];
}

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDict:(NSDictionary*)tradeDict  {
    NSDictionary* itemDict = [QSTradeUtil getItemSnapshot:tradeDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.itemNameLabel.text = [QSItemUtil getItemName:itemDict];

    NSString *oldPrice = [NSString stringWithFormat:@"原价: %@",[QSItemUtil getPriceDesc:itemDict]];
    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:oldPrice];
    [attri addAttribute:NSStrikethroughStyleAttributeName value:@(NSUnderlinePatternSolid | NSUnderlineStyleSingle) range:NSMakeRange(0, oldPrice.length)];
    [attri addAttribute:NSStrikethroughColorAttributeName value:[UIColor colorWithWhite:0.353 alpha:1.000] range:NSMakeRange(0, oldPrice.length)];
    [self.priceLabel setAttributedText:attri];
    self.prompPriceLabel.text = [NSString stringWithFormat:@"现价: %@",[QSItemUtil getPromoPriceDesc:itemDict]];

    self.propNameLabel1.text = [QSTradeUtil getPropertiesDesc:tradeDict];
    NSNumber* price = [QSItemUtil getPromoPrice:[QSTradeUtil getItemSnapshot:tradeDict]];
    int disCount = [QSTradeUtil getExpectedPrice:tradeDict].doubleValue * 100 / price.doubleValue;
    if (disCount < 10) {
        disCount = 10;
    }else if(disCount > 90)
    {
        disCount = 90;
    }else
    {
        if (disCount%10 > 5) {
            disCount = (disCount/10+1)*10;
        }
    }
    disCount = disCount/10;
}

+ (CGFloat)cellHeight {
    return 155.f;
}
@end
