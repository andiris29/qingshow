//
//  QS11TextCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QS11TextCell.h"
#import "UINib+QSExtension.h"
#import "QSTradeUtil.h"
#import "QSItemUtil.h"

@implementation QS11TextCell

+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QS11TextCell"];
}

- (void)awakeFromNib {
    // Initialization code
  
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)bindWithDict:(NSDictionary*)tradeDict actualPrice:(NSNumber *)actualPrice {
    //NSNumber* nowPrice = [QSTradeUtil getActualPrice:tradeDict];
    NSNumber* price = [QSItemUtil getPrice:[QSTradeUtil getItemSnapshot:tradeDict]];
    int disCount = actualPrice.doubleValue * 100 / price.doubleValue;
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
  
    self.actualDiscountLabel.text = [NSString stringWithFormat:@"%d折", disCount];
    NSString *nowPrice = [NSString stringWithFormat:@"￥%@",actualPrice];
    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:nowPrice];
    NSRange contentRange = {0,[attri length]};
    [attri addAttribute:NSUnderlineStyleAttributeName value:[NSNumber numberWithInteger:NSUnderlineStyleSingle] range:contentRange];
    [self.actualPriceLabel setAttributedText:attri];

}
@end
