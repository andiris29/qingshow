//
//  QSTotalPriceCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeTableViewCellBase.h"

@interface QSTotalPriceCell : QSCreateTradeTableViewCellBase

@property (strong, nonatomic) IBOutlet UILabel* priceLabel;
@property (strong, nonatomic) IBOutlet UIButton* submitBtn;

- (IBAction)submitBtnPressed:(id)sender;

@end
