//
//  QSDiscountQuantityCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractDiscountTableViewCell.h"
@interface QSDiscountQuantityCell : QSAbstractDiscountTableViewCell

@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;
@property (weak, nonatomic) IBOutlet UIButton* addBtn;
@property (weak, nonatomic) IBOutlet UIButton* minusBtn;

@property (readonly, nonatomic) int quantity;
@end
