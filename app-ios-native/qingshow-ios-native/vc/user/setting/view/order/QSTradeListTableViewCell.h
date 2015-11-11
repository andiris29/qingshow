//
//  QSTradeListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define QSTradeListTableViewCellIdentifier @"QSTradeListTableViewCellIdentifier"
#define QSTradeListTableViewCellHeight 230

@class QSTradeListTableViewCell;

@protocol QSTradeListTableViewCellDelegate <NSObject>

- (void)didClickRefundBtnForCell:(QSTradeListTableViewCell*)cell;

- (void)didClickPayBtnForCell:(QSTradeListTableViewCell*)cell;

- (void)didClickReceiveBtnForCell:(QSTradeListTableViewCell *)cell;

- (void)didClickCancelBtnForCell:(QSTradeListTableViewCell *)cell;

- (void)didClickExpectablePriceBtnForCell:(QSTradeListTableViewCell *)cell;

- (void)didClickToWebPageForCell:(QSTradeListTableViewCell *)cell;

- (void)didClickLogisticForCell:(QSTradeListTableViewCell *)cell;

@end

typedef NS_ENUM(NSUInteger, QSTradeListTableViewCellType) {
    QSTradeListTableViewCellNormal,
    QSTradeListTableViewCellComplete
};

@interface QSTradeListTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* stateLabel;
@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView* itemImgView;

@property (weak, nonatomic) IBOutlet UILabel* sizeLabel;
@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;

@property (weak, nonatomic) IBOutlet UILabel *dateLabel;
@property (weak, nonatomic) IBOutlet UILabel *originPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *nowPriceLabel;

@property (weak, nonatomic) IBOutlet UIButton *clickToWebpageBtn;

@property (weak, nonatomic) IBOutlet UIImageView *circleBtnImageView;
@property (assign, nonatomic) QSTradeListTableViewCellType cellType;

//确认收货
@property (strong, nonatomic) IBOutlet UIButton* submitButton;
- (IBAction)submitBtnPressed:(id)sender;
//申请退货
@property (strong, nonatomic) IBOutlet UIButton *refundButton;
- (IBAction)refundBtnPressed:(id)sender;
//物流信息
@property (strong, nonatomic) IBOutlet UIButton* logisticsButton;
- (IBAction)logisticsBtnPressed:(id)sender;
//取消预定
@property (strong, nonatomic) IBOutlet UIButton* cancelButton;
- (IBAction)cancelBtnPressed:(id)sender;
//查看折扣
@property (strong, nonatomic) IBOutlet UIButton* showDiscountButton;
- (IBAction)showDiscountBtnPressed:(id)sender;
//立即付款
@property (strong, nonatomic) IBOutlet UIButton* payButton;
- (IBAction)payBtnPressed:(id)sender;


- (IBAction)clickToWebpageBtnPressed:(id)sender;

- (void)bindWithDict:(NSDictionary*)dict;

@property (weak, nonatomic) NSObject<QSTradeListTableViewCellDelegate>* delegate;
//@property (assign,nonatomic) int type;
@property (weak, nonatomic) IBOutlet UILabel* messageLabel;
@end
