//
//  QSDiscountResultCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractDiscountTableViewCell.h"
@interface QSDiscountResultCell : QSAbstractDiscountTableViewCell

@property (weak, nonatomic) IBOutlet UILabel* discountRateLabel;
@property (weak, nonatomic) IBOutlet UILabel* totalPriceLabel;

@end
