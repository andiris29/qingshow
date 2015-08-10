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
#import "QSOrderUtil.h"

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
    NSDictionary* orderDict = [QSTradeUtil getFirstOrder:dict];
    NSDictionary* itemDict = [QSOrderUtil getItemSnapshot:orderDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    
    if ([QSOrderUtil getActualPrice:orderDict]) {
        self.priceAfterDiscountLabel.text = [QSOrderUtil getActualPriceDesc:orderDict];
    } else {
        self.priceAfterDiscountLabel.text = [QSOrderUtil getExpectedPriceDesc:orderDict];
    }
    
    self.priceLabel.text = [QSItemUtil getPriceDesc:itemDict];
    [self.priceLabel sizeToFit];
    [self.priceAfterDiscountLabel sizeToFit];
}

@end
