//
//  QSTradeListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define QSTradeListTableViewCellIdentifier @"QSTradeListTableViewCellIdentifier"
#define QSTradeListTableViewCellHeight 182

@class QSTradeListTableViewCell;

@protocol QSTradeListTableViewCellDelegate <NSObject>

- (void)didClickRefundBtnForCell:(QSTradeListTableViewCell*)cell;
- (void)didClickLogisticForCell:(QSTradeListTableViewCell *)cell;

- (void)didClickToWebPageForCell:(QSTradeListTableViewCell *)cell;

@end


@interface QSTradeListTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* stateLabel;
@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView* itemImgView;

@property (weak, nonatomic) IBOutlet UILabel* sizeLabel;
@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;

@property (weak, nonatomic) IBOutlet UILabel *dateLabel;
@property (weak, nonatomic) IBOutlet UILabel *priceLabel;

@property (weak, nonatomic) IBOutlet UIButton *clickToWebpageBtn;


//申请退货
@property (strong, nonatomic) IBOutlet UIButton *refundButton;
- (IBAction)refundBtnPressed:(id)sender;
//物流信息
@property (strong, nonatomic) IBOutlet UIButton* logisticsButton;
- (IBAction)logisticsBtnPressed:(id)sender;
- (IBAction)clickToWebpageBtnPressed:(id)sender;

- (void)bindWithDict:(NSDictionary*)dict;

@property (weak, nonatomic) NSObject<QSTradeListTableViewCellDelegate>* delegate;
@end
