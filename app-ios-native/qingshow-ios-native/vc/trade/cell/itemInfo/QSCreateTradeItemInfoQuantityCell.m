//
//  QSCreateTradeItemInfoQuantityCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoQuantityCell.h"
#import <QuartzCore/QuartzCore.h>
#import "QSTradeUtil.h"

@implementation QSCreateTradeItemInfoQuantityCell
- (void)awakeFromNib
{
    [super awakeFromNib];
//    [self configBorderCornerBtn:self.numberTextField withColor:[UIColor colorWithRed:169.f/255.f green:26.f/255.f blue:78.f/255.f alpha:1.f]];
}

- (void)configBorderCornerBtn:(UIView*)btn withColor:(UIColor*)color
{
    btn.layer.cornerRadius = 4.f;
    btn.layer.masksToBounds = YES;
    btn.layer.borderColor = color.CGColor;
    btn.layer.borderWidth = 1.f;
}
- (void)bindWithDict:(NSDictionary *)tradeDict {
    self.numberTextField.text = [QSTradeUtil getQuantityDesc:tradeDict];
}

@end