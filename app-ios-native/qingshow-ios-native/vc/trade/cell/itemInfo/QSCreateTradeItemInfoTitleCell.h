//
//  QSCreateTradeItemInfoTitleCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeHeight80Cell.h"
#import "UILabelStrikeThrough.h"

@interface QSCreateTradeItemInfoTitleCell : QSCreateTradeTableViewCellBase

@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UILabelStrikeThrough* priceLabel;

@property (weak, nonatomic) IBOutlet UIImageView* iconImageView;
@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;
@property (weak, nonatomic) IBOutlet UILabel* selectedSkuLabel;

@end
