//
//  QSCreateTradeSkuPropertyCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 8/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeTableViewCellBase.h"

@interface QSCreateTradeSkuPropertyCell : QSCreateTradeTableViewCellBase

+ (QSCreateTradeSkuPropertyCell*)generateCell;

@property (weak, nonatomic) IBOutlet UILabel* leftLabel;
@property (weak, nonatomic) IBOutlet UILabel* rightLabel;
- (void)bindWithDict:(NSDictionary *)dict;
- (void)bindSkuProperty:(NSString*)skuProperty;

@end
