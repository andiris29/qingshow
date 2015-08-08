//
//  QS11TextCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QS11TextCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* actualPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel* actualDiscountLabel;

+ (instancetype)generateView;
- (void)bindWithDict:(NSDictionary*)tradeDict;
@end
