//
//  QSCreateTradeItemInfoTitleCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoTitleCell.h"
#import "QSItemUtil.h"

@implementation QSCreateTradeItemInfoTitleCell

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)bindWithDict:(NSDictionary*)dict
{
    self.titleLabel.text = [QSItemUtil getItemName:dict];
    [self updateWithItemPrice:dict];
}

- (void)updateWithItemPrice:(NSDictionary*)itemDict {
    self.priceTextLabel.hidden = YES;
    self.priceLabel.hidden = YES;
    self.priceLabel.text = @"";

    NSNumber* totalPrice = [QSItemUtil getPriceAfterDiscount:itemDict];
    
    self.priceAfterDiscountLabel.text = [NSString stringWithFormat:@"%.2f", totalPrice.doubleValue];
    [self.priceLabel sizeToFit];
    [self.priceAfterDiscountLabel sizeToFit];
}
@end
