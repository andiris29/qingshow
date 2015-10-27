//
//  QS11TextCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSS12TextCellDelegate <NSObject>

- (void)didClickShareToPayOfCell:(UITableViewCell*)cell;

@end

@interface QSS12TradeDiscountCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* actualPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel* actualDiscountLabel;
@property (weak, nonatomic) IBOutlet UILabel* dotView;

@property (weak, nonatomic) IBOutlet UILabel* messageLabel;
@property (weak, nonatomic) NSObject<QSS12TextCellDelegate>* delegate;


+ (instancetype)generateView;
- (void)bindWithDict:(NSDictionary*)tradeDict actualPrice:(NSNumber *)actualPrice;
- (IBAction)shareToBuyBtnPressed:(id)sender;
+ (CGFloat)cellHeight;
@end
