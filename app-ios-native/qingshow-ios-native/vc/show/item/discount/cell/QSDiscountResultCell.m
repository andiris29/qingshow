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
@property (assign, nonatomic) int maxDisCount;


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
    
    self.minDiscount = 3;
    self.currentDiscount = 3;
    self.quantity = 1;
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
    if (self.currentDiscount < 3) {
        self.currentDiscount = 3;
    }
    if(self.currentDiscount > self.maxDisCount )
    {
        self.currentDiscount = self.maxDisCount;
    }
    if (self.currentDiscount > 9) {
        self.currentDiscount = 9;
    }
    [self updateUi];
}

- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 114.f;
}

- (void)bindWithData:(NSDictionary *)itemDict {
    if (self.itemDict == itemDict) {
        return;
    }
    
    self.itemDict = itemDict;
    NSNumber* minExpectionPrice = [QSItemUtil getMinExpectionPrice:self.itemDict];
    if (minExpectionPrice) {
        self.minDiscount = (int) ([QSItemUtil getMinExpectionPrice:self.itemDict].doubleValue * 10 / [QSItemUtil getPromoPrice:self.itemDict].doubleValue);
    } else {
#warning 写死3折   现在已修改为先显示最高的折扣
        float promoPrice = [QSItemUtil getPromoPrice:self.itemDict].floatValue;
        float price = [QSItemUtil getPrice:self.itemDict].floatValue;
        
        self.minDiscount = (promoPrice/price)*10;
    }

    self.currentDiscount = self.minDiscount;
    self.maxDisCount = self.minDiscount;
    [self updateUi];
}

- (void)updateUi {
    self.discountRateLabel.text = [NSString stringWithFormat:@"%d折", self.currentDiscount];
    NSNumber* finalPrice = [self getFinalPrice];
    self.totalPriceLabel.text = [NSString stringWithFormat:@"%.2f", finalPrice.doubleValue];
    
}

- (void)setQuantity:(int)quantity {
    _quantity = quantity;
    [self updateUi];
}
- (NSNumber*)getSinglePrice {
    return @([QSItemUtil getPromoPrice:self.itemDict].doubleValue * self.currentDiscount / 10);
}
- (NSNumber*)getFinalPrice {
    return @([QSItemUtil getPrice:self.itemDict].doubleValue * self.quantity * self.currentDiscount / 10);
}
@end
