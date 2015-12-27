//
//  QSBuyerCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/26.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractDiscountTableViewCell.h"
@interface QSBuyerCell : QSAbstractDiscountTableViewCell

@property (strong, nonatomic) IBOutlet UILabel* titleLabel;
@property (strong, nonatomic) IBOutlet UIView* buyerContainerView;
+ (instancetype)generateCell;
- (void)bindWithBuyerInfo:(NSDictionary*)dict;
@end
