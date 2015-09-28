//
//  QSCreateTradeItemInfoTitleCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoTitleCell.h"
#import "QSItemUtil.h"
#import "QSTradeUtil.h"

@implementation QSCreateTradeItemInfoTitleCell
- (void)awakeFromNib {
    [super awakeFromNib];
    self.priceLabel.isWithStrikeThrough = YES;
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)bindWithDict:(NSDictionary*)dict
{
    NSDictionary* tradeDict = dict;
    NSDictionary* itemDict = [QSTradeUtil getItemDic:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    if ([QSTradeUtil getActualPrice:tradeDict]) {
        self.priceAfterDiscountLabel.text = [QSTradeUtil getActualPriceDesc:tradeDict];
    } else {
        self.priceAfterDiscountLabel.text = [NSString stringWithFormat:@"%@",[QSItemUtil getExpectablePrice:itemDict]];
    }
    
    self.priceLabel.text = [QSItemUtil getPriceDesc:itemDict];
    [self.priceLabel sizeToFit];
    [self.priceAfterDiscountLabel sizeToFit];
}

@end
