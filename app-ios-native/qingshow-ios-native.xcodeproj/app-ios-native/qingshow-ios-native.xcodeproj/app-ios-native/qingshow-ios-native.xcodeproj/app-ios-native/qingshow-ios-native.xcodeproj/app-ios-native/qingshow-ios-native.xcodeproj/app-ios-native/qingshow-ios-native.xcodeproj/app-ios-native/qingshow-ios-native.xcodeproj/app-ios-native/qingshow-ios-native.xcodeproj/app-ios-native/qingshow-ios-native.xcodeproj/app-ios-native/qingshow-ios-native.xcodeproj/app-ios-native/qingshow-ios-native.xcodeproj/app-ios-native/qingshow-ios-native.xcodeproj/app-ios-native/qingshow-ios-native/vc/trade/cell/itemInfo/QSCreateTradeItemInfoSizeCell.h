//
//  QSCreateTradeItemInfoSizeCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeColorAndSizeBaseTableViewCell.h"

@interface QSCreateTradeItemInfoSizeCell : QSCreateTradeColorAndSizeBaseTableViewCell

- (void)updateWithColorSelected:(NSString*)sku item:(NSDictionary*)itemDict;
@end
