//
//  QSDiscountQuantityCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountQuantityCell.h"
#import "UINib+QSExtension.h"
#import <QuartzCore/QuartzCore.h>
@implementation QSDiscountQuantityCell

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
    self.addBtn.layer.masksToBounds = YES;
    self.addBtn.layer.cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    self.minusBtn.layer.masksToBounds = YES;
    self.minusBtn.layer.cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    
    self.quantityLabel.layer.borderWidth = 1.f;
    self.quantityLabel.layer.borderColor = [UIColor colorWithRed:155.f/255.f green:155.f/255.f blue:155.f/255.f alpha:1.f].CGColor;
    self.quantityLabel.layer.masksToBounds = YES;
    self.quantityLabel.layer.cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    self.dotView.layer.cornerRadius = self.dotView.bounds.size.width/2;
    self.dotView.layer.masksToBounds = YES;
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
    int current = self.quantityLabel.text.intValue;
    current += delta;
    if (current <= 1) {
        current = 1;
        self.minusBtn.backgroundColor = [UIColor colorWithWhite:0.627 alpha:1.000];
    }
    else
    {
        self.minusBtn.backgroundColor = [UIColor colorWithRed:0.953 green:0.584 blue:0.643 alpha:1.000];
    }
    self.quantityLabel.text = [NSString stringWithFormat:@"%d", current];
    if ([self.delegate respondsToSelector:@selector(updateTotalPrice)]) {
        [self.delegate updateTotalPrice];
    }
}
- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 80.f;
}
- (int)quantity {
    return self.quantityLabel.text.intValue;
}
@end
