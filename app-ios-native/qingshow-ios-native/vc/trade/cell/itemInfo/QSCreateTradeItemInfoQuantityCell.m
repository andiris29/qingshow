//
//  QSCreateTradeItemInfoQuantityCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoQuantityCell.h"
#import <QuartzCore/QuartzCore.h>

@implementation QSCreateTradeItemInfoQuantityCell
- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self configBorderCornerBtn:self.minusBtn withColor:[UIColor colorWithRed:198.f/255.f green:198.f/255.f blue:198.f/255.f alpha:1.f]];
    [self configBorderCornerBtn:self.plusBtn withColor:[UIColor colorWithRed:198.f/255.f green:198.f/255.f blue:198.f/255.f alpha:1.f]];
    [self configBorderCornerBtn:self.numberTextField withColor:[UIColor colorWithRed:169.f/255.f green:26.f/255.f blue:78.f/255.f alpha:1.f]];
}

- (void)configBorderCornerBtn:(UIView*)btn withColor:(UIColor*)color
{
    btn.layer.cornerRadius = 4.f;
    btn.layer.masksToBounds = YES;
    btn.layer.borderColor = color.CGColor;
    btn.layer.borderWidth = 1.f;
}


- (IBAction)plusBtnPressed:(id)sender
{
    [self adjustQuantityBy:1];
}
- (IBAction)minusBtnPressed:(id)sender
{
    [self adjustQuantityBy:-1];
}
- (void)adjustQuantityBy:(int)num
{
    int quantity = self.numberTextField.text.intValue;
    quantity += num;
    quantity = quantity > 0 ? quantity : 1;
    self.numberTextField.text = @(quantity).stringValue;
    
    if ([self.delegate respondsToSelector:@selector(updateCellTriggerBy:)]) {
        [self.delegate updateCellTriggerBy:self];
    }
    
}

- (id)getInputData
{
    return @(self.numberTextField.text.intValue);
}

@end
