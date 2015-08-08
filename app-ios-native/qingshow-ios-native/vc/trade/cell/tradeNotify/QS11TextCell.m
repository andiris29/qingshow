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
- (void)bindWithDict:(NSDictionary*)tradeDict {
    NSNumber* actualPrice = [QSTradeUtil getActualPrice:tradeDict];
    NSNumber* price = [QSItemUtil getPrice:[QSTradeUtil getItemSnapshot:tradeDict]];
    self.actualDiscountLabel.text = [NSString stringWithFormat:@"%d%%", (int)(actualPrice.doubleValue * 100 / price.doubleValue)];
    self.actualPriceLabel.text = [QSTradeUtil getActualPriceDesc:tradeDict];
}
@end
