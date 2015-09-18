//
//  QST01ShowTradeCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QST01ShowTradeCellDelegate <NSObject>

- (void)didtapHeaderInT01VC:(NSDictionary *)peopleDic;

@end

@interface QST01ShowTradeCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIImageView *headerImgView;
@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *disCountLabel;
@property (weak, nonatomic) IBOutlet UILabel *actualPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *clothNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *priceLabel;
@property (weak, nonatomic) IBOutlet UILabel *promoPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;
@property (weak, nonatomic) IBOutlet UILabel *outOfSaleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *itemImgView;

@property (weak,nonatomic) NSObject<QST01ShowTradeCellDelegate>* delegate;

- (void)bindWithDic:(NSDictionary *)dict;
@end
