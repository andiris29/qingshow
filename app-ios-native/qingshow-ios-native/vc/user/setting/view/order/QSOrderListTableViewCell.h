//
//  QSOrderListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define QSOrderListTableViewCellIdentifier @"QSOrderListTableViewCellIdentifier"
#define QSOrderListTableViewCellHeight 172


@class QSOrderListTableViewCell;

@protocol QSOrderListTableViewCellDelegate <NSObject>

- (void)didClickRefundBtnForCell:(QSOrderListTableViewCell*)cell;
- (void)didClickLogisticBtnForCell:(QSOrderListTableViewCell*)cell;
- (void)didClickSubmitBtnForCell:(QSOrderListTableViewCell*)cell;

@end

typedef NS_ENUM(NSUInteger, QSOrderListTableViewCellType) {
    QSOrderListTableViewCellTypeComplete,
    QSOrderListTableViewCellTypeWaiting
};

@interface QSOrderListTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* orderIdLabel;
@property (weak, nonatomic) IBOutlet UILabel* stateLabel;
@property (weak, nonatomic) IBOutlet UILabel* titleLabel;

@property (weak, nonatomic) IBOutlet UILabel* sizeLabel;
@property (weak, nonatomic) IBOutlet UILabel* quantityLabel;
@property (weak, nonatomic) IBOutlet UILabel* colorLabel;
@property (weak, nonatomic) IBOutlet UILabel* priceLabel;

@property (weak, nonatomic) IBOutlet UILabel* dateStartTextLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateStartLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateEndTextLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateEndLabel;

@property (weak, nonatomic) IBOutlet UIButton* refundButton;
@property (weak, nonatomic) IBOutlet UIButton* logisticButton;
@property (weak, nonatomic) IBOutlet UIButton* submitButton;

@property (assign, nonatomic) QSOrderListTableViewCellType type;

- (IBAction)refundBtnPressed:(id)sender;
- (IBAction)logisticBtnPressed:(id)sender;
- (IBAction)submitBtnPressed:(id)sender;

- (void)bindWithDict:(NSDictionary*)dict;

@property (weak, nonatomic) NSObject<QSOrderListTableViewCellDelegate>* delegate;
@end
