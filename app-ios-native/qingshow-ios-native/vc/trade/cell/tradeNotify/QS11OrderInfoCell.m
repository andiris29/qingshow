//
//  QS11OrderInfoCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QS11OrderInfoCell.h"
#import "UINib+QSExtension.h"
#import "QSTradeUtil.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QS11OrderInfoCell
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QS11OrderInfoCell"];
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
    self.priceLabel.text = [NSString stringWithFormat:@"原价：%@", [QSItemUtil getPriceDesc:itemDict]];
    self.prompPriceLabel.text = [NSString stringWithFormat:@"现价：%@",[QSItemUtil getPromoPriceDesc:itemDict]];
    
//    NSArray* prop = [QSTradeUtil getSkuProperties:tradeDict];
//    if (prop.count > 0) {
//        NSString* p = prop[0];
//        self.propNameLabel1.text = p;
//    }else {
//        self.propNameLabel1.text = @"";
//    }
//    if (prop.count > 1) {
//        NSString* p = prop[1];
//        self.propNameLabel2.text = p;
//    }else {
//        self.propNameLabel2.text = @"";
//    }
    self.propNameLabel1.text = [QSTradeUtil getSizeText:tradeDict];

    NSNumber* price = [QSItemUtil getPrice:[QSTradeUtil getItemSnapshot:tradeDict]];
    int disCount = [QSItemUtil getPromoPrice:itemDict].doubleValue * 100 / price.doubleValue;
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
    
    self.expectDiscountLabel.text = [NSString stringWithFormat:@"期望折扣：%d折",disCount];
    self.expectedPriceLabel.text = [NSString stringWithFormat:@"期望价格 :%@", [QSTradeUtil getExpectedPriceDesc:tradeDict]];
}
@end
