//
//  QSCreateTradeItemInfoTitleCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeHeigh60Cell.h"

@interface QSCreateTradeItemInfoTitleCell : QSCreateTradeHeigh60Cell

@property (strong, nonatomic) IBOutlet UILabel* titleLabel;
@property (strong, nonatomic) IBOutlet UILabel* priceAfterDiscountLabel;
@property (strong, nonatomic) IBOutlet UILabel* priceTextLabel;
@property (strong, nonatomic) IBOutlet UILabel* priceLabel;

- (void)updateWithSize:(NSString*)sizeSku color:(NSString*)colorSku item:(NSDictionary*)itemDict;

@end
