//
//  QSCreateTradeItemInfoColorCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/28/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeHeigh60Cell.h"

@interface QSCreateTradeItemInfoColorCell : QSCreateTradeHeigh60Cell

@property (strong, nonatomic) IBOutlet UILabel* colorLabel;

- (void)bindWithDict:(NSDictionary*)dict;

@end
