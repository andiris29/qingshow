//
//  QSDiscountQuantityCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountQuantityCell.h"
#import "UINib+QSExtension.h"
#import "UIView+QSExtension.h"
#import <QuartzCore/QuartzCore.h>
@implementation QSDiscountQuantityCell

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
    
    UIColor* borderColor = [UIColor colorWithRed:155.f/255.f green:155.f/255.f blue:155.f/255.f alpha:1.f];;
    CGFloat borderWidth = 1.f;
    CGFloat cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    
//    [self.addBtn configBorderColor:borderColor width:borderWidth cornerRadius:cornerRadius];
    [self.minusBtn configBorderColor:borderColor width:borderWidth cornerRadius:cornerRadius];
    [self.addBtn configBorderColor:borderColor width:0 cornerRadius:cornerRadius];
    [self.quantityLabel configBorderColor:borderColor width:borderWidth cornerRadius:cornerRadius];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountQuantityCell"];
}

- (IBAction)increaseBtnPressed:(id)sender {
    [self adjustNumber:1];
}

- (IBAction)decreaseBtnPressed:(id)sender {
    [self adjustNumber:-1];
}
- (void)adjustNumber:(int)delta {
    UIColor* borderColor = [UIColor colorWithRed:155.f/255.f green:155.f/255.f blue:155.f/255.f alpha:1.f];;
    CGFloat cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    int current = self.quantityLabel.text.intValue;
    current += delta;
    if (current <= 1) {
        current = 1;
        self.minusBtn.backgroundColor = [UIColor whiteColor];
        [self.minusBtn setTitleColor:[UIColor colorWithRed:159.f/255.f green:159.f/255.f blue:159.f/255.f alpha:1.f] forState:UIControlStateNormal];
        [self.minusBtn configBorderColor:borderColor width:1 cornerRadius:cornerRadius];
    }
    else
    {
        self.minusBtn.backgroundColor = [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f];
        [self.minusBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [self.minusBtn configBorderColor:borderColor width:0 cornerRadius:cornerRadius];
    }
    self.quantityLabel.text = [NSString stringWithFormat:@"%d", current];
    if ([self.delegate respondsToSelector:@selector(discountCellUpdateTotalPrice:)]) {
        [self.delegate discountCellUpdateTotalPrice:self];
    }
}
- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 50.f;
}
- (int)quantity {
    return self.quantityLabel.text.intValue;
}
@end
