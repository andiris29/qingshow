//
//  QSCreateTradeSkuPropertyCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 8/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeHeigh40Cell.h"

@interface QSCreateTradeSkuPropertyCell : QSCreateTradeHeigh40Cell

+ (QSCreateTradeSkuPropertyCell*)generateCell;

@property (weak, nonatomic) IBOutlet UILabel* leftLabel;
@property (weak, nonatomic) IBOutlet UILabel* rightLabel;
- (void)bindWithDict:(NSDictionary *)dict;
- (void)bindSkuProperty:(NSString*)skuProperty;

@end
