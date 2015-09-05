//
//  QSOrderListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define QSOrderListTableViewCellIdentifier @"QSOrderListTableViewCellIdentifier"
#define QSOrderListTableViewCellHeight 216

@class QSOrderListTableViewCell;

@protocol QSOrderListTableViewCellDelegate <NSObject>

- (void)didClickRefundBtnForCell:(QSOrderListTableViewCell*)cell;
//- (void)didClickSubmitBtnForCell:(QSOrderListTableViewCell*)cell;

- (void)didClickPayBtnForCell:(QSOrderListTableViewCell*)cell;

- (void)didClickExchangeBtnForCell:(QSOrderListTableViewCell *)cell;

- (void)didClickReceiveBtnForCell:(QSOrderListTableViewCell *)cell;

- (void)didClickCancelBtnForCell:(QSOrderListTableViewCell *)cell;
@end

@interface QSOrderListTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* stateLabel;
@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView* itemImgView;

@property (weak, nonatomic) IBOutlet UILabel* sizeLabel;
@property (weak, nonatomic) IBOutlet UILabel* sizeTextLabel;
@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;
@property (weak, nonatomic) IBOutlet UILabel* quantityTextLabel;
@property (weak, nonatomic) IBOutlet UILabel* colorLabel;
@property (weak, nonatomic) IBOutlet UILabel* colorTextLabel;
@property (weak, nonatomic) IBOutlet UILabel* priceLabel;
@property (weak, nonatomic) IBOutlet UILabel* priceTextLabel;

@property (weak, nonatomic) IBOutlet UILabel* dateStartTextLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateStartLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateEndTextLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateEndLabel;

@property (weak, nonatomic) IBOutlet UIButton* submitButton;
@property (weak, nonatomic) IBOutlet UIButton *exchangeButton;
@property (weak, nonatomic) IBOutlet UIButton *returnButton;
@property (weak, nonatomic) IBOutlet UIImageView *saleImgView;
@property (weak, nonatomic) IBOutlet UILabel *dateLabel;
@property (weak, nonatomic) IBOutlet UILabel *originPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *nowPriceLabel;
@property (weak, nonatomic) IBOutlet UILabel *exDiscountLabel;

#warning TODO
@property (weak, nonatomic) IBOutlet UIView* currentDiscountContainer;

- (IBAction)submitBtnPressed:(id)sender;
- (IBAction)returnBtnPressed:(id)sender;
- (IBAction)exchangeBtnPressed:(id)sender;

- (void)bindWithDict:(NSDictionary*)dict;

@property (weak, nonatomic) NSObject<QSOrderListTableViewCellDelegate>* delegate;
@property (assign,nonatomic) int type;
@end
