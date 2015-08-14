//
//  QSDiscountResultCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountResultCell.h"
#import "UINib+QSExtension.h"
#import "QSItemUtil.h"

@interface QSDiscountResultCell ()

@property (strong, nonatomic) NSDictionary* itemDict;

@property (assign, nonatomic) int minDiscount;
@property (assign, nonatomic) int maxDiscount;
@property (assign, nonatomic) int currentDiscount;

@end

@implementation QSDiscountResultCell

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
    
    self.addBtn.layer.masksToBounds = YES;
    self.addBtn.layer.cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    self.minusBtn.layer.masksToBounds = YES;
    self.minusBtn.layer.cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;
    
    self.discountRateLabel.layer.borderWidth = 1.f;
    self.discountRateLabel.layer.borderColor = [UIColor colorWithRed:155.f/255.f green:155.f/255.f blue:155.f/255.f alpha:1.f].CGColor;
    self.discountRateLabel.layer.masksToBounds = YES;
    self.discountRateLabel.layer.cornerRadius = DISCOUNT_CELL_CORNER_RADIUS;

    self.quantity = 1;
    
//    self.dotView.layer.cornerRadius = self.dotView.bounds.size.width / 2;
//    self.dotView.layer.masksToBounds = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountResultCell"];
}

- (IBAction)increaseBtnPressed:(id)sender {
    [self adjustDiscount:1];
}
- (IBAction)decreateBtnPressed:(id)sender {
    [self adjustDiscount:-1];
}
- (void)adjustDiscount:(int)delta {
    self.currentDiscount += delta;
    
    if (self.currentDiscount > self.maxDiscount) {
        self.currentDiscount = self.maxDiscount;
    }
    if (self.currentDiscount < self.minDiscount) {
        self.currentDiscount = self.minDiscount;
    }
    
    [self updateUi];
}

- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 90.f;
}

- (void)bindWithData:(NSDictionary *)itemDict {
    if (self.itemDict == itemDict) {
        return;
    }
    
    self.itemDict = itemDict;
    
    self.maxDiscount = 9;
    self.currentDiscount = self.maxDiscount;
    NSNumber* promoPrice = [QSItemUtil getPromoPrice:self.itemDict];
    NSNumber* minExpectionPrice = [QSItemUtil getMinExpectionPrice:self.itemDict];
    if (minExpectionPrice) {
        self.minDiscount = (int) (minExpectionPrice.doubleValue * 10 / promoPrice.doubleValue);
        self.minDiscount = self.minDiscount < 5 ? self.minDiscount : 5;
    } else {
        self.minDiscount = 5;
    }
    
    [self updateUi];
}

- (void)updateUi {
    self.discountRateLabel.text = [NSString stringWithFormat:@"%d折", self.currentDiscount];
    NSNumber* finalPrice = [self getFinalPrice];
    self.totalPriceLabel.text = [NSString stringWithFormat:@"￥%.2f", finalPrice.doubleValue];
    
    if (self.currentDiscount <= self.minDiscount) {
        self.minusBtn.backgroundColor = [UIColor colorWithWhite:0.627 alpha:1.000];
    } else {
        self.minusBtn.backgroundColor = [UIColor colorWithRed:0.953 green:0.584 blue:0.643 alpha:1.000];
    }
    if(self.currentDiscount >= self.maxDiscount ) {
        self.addBtn.backgroundColor = [UIColor colorWithWhite:0.627 alpha:1.000];
    } else {
        self.addBtn.backgroundColor = [UIColor colorWithRed:0.953 green:0.584 blue:0.643 alpha:1.000];
    }
}

- (void)setQuantity:(int)quantity {
    _quantity = quantity;
    [self updateUi];
}
- (NSNumber*)getSinglePrice {
    return @([QSItemUtil getPromoPrice:self.itemDict].doubleValue * self.currentDiscount / 10);
}
- (NSNumber*)getFinalPrice {
    return @([QSItemUtil getPromoPrice:self.itemDict].doubleValue * self.quantity * self.currentDiscount / 10);
}
@end
