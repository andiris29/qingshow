//
//  QS11OrderInfoCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QS11OrderInfoCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIImageView* itemImgView;
@property (weak, nonatomic) IBOutlet UILabel* itemNameLabel;
@property (weak, nonatomic) IBOutlet UILabel* priceLabel;
@property (weak, nonatomic) IBOutlet UILabel* prompPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel* propNameLabel1;
@property (weak, nonatomic) IBOutlet UILabel* propNameLabel2;
@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;
@property (weak, nonatomic) IBOutlet UILabel* expectDiscountLabel;
@property (weak, nonatomic) IBOutlet UILabel* expectedPriceLabel;

+ (instancetype)generateView;
- (void)bindWithDict:(NSDictionary*)tradeDict;
@end
