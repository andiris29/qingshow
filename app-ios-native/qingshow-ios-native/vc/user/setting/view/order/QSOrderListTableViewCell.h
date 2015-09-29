//
//  QSOrderListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define QSOrderListTableViewCellIdentifier @"QSOrderListTableViewCellIdentifier"
#define QSOrderListTableViewCellHeight 263

@class QSOrderListTableViewCell;

@protocol QSOrderListTableViewCellDelegate <NSObject>

- (void)didClickRefundBtnForCell:(QSOrderListTableViewCell*)cell;

- (void)didClickPayBtnForCell:(QSOrderListTableViewCell*)cell;

- (void)didClickReceiveBtnForCell:(QSOrderListTableViewCell *)cell;

- (void)didClickCancelBtnForCell:(QSOrderListTableViewCell *)cell;

- (void)didClickExpectablePriceBtnForCell:(QSOrderListTableViewCell *)cell;

- (void)didClickToWebPageForCell:(QSOrderListTableViewCell *)cell;

@end

@interface QSOrderListTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* stateLabel;
@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView* itemImgView;

@property (weak, nonatomic) IBOutlet UILabel* sizeLabel;
@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;

@property (weak, nonatomic) IBOutlet UILabel* priceLabel;


@property (weak, nonatomic) IBOutlet UILabel *dateLabel;
@property (weak, nonatomic) IBOutlet UILabel *originPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *nowPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *exDiscountLabel;

@property (weak, nonatomic) IBOutlet UIButton *clickToWebpageBtn;

@property (weak, nonatomic) IBOutlet UILabel *hintLabel;

@property (weak, nonatomic) IBOutlet UIImageView *circleBtnImageView;


//确认收货
@property (strong, nonatomic) IBOutlet UIButton* submitButton;
- (IBAction)submitBtnPressed:(id)sender;
//申请退货
@property (strong, nonatomic) IBOutlet UIButton *refundButton;
- (IBAction)refundBtnPressed:(id)sender;
//物流信息
@property (strong, nonatomic) IBOutlet UIButton* logisticsButton;
- (IBAction)logisticsBtnPressed:(id)sender;
//取消申请
@property (strong, nonatomic) IBOutlet UIButton* cancelButton;
- (IBAction)cancelBtnPressed:(id)sender;



- (IBAction)clickToWebpageBtnPressed:(id)sender;

- (void)bindWithDict:(NSDictionary*)dict;

@property (weak, nonatomic) NSObject<QSOrderListTableViewCellDelegate>* delegate;
@property (assign,nonatomic) int type;
@property (weak, nonatomic) IBOutlet UILabel* messageLabel;
@end
