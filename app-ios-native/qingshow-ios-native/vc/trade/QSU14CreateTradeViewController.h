//
//  QSCreateTradeViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/15/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSCreateTradeTableViewCellBase.h"
#import "QSCreateTradeItemInfoTitleCell.h"
#import "QSU10ReceiverListViewController.h"
#import "QSCreateTradeReceiverInfoTextCell.h"
#import "QSCreateTradeReceiverInfoLocationCell.h"
#import "QSCreateTradePayInfoSelectCell.h"
#import "QSLocationPickerProvider.h"

@interface QSU14CreateTradeViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, QSU10ReceiverListViewControllerDelegate, QSLocationPickerProviderDelegate, UIAlertViewDelegate>

#pragma mark - Item Info Cells
@property (strong, nonatomic) IBOutlet QSCreateTradeItemInfoTitleCell *itemInfoTitleCell;

#pragma mark - Receiver Info Cell
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *receiverInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoTextCell *receiverInfoNameCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoTextCell *receiverInfoPhoneCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoLocationCell *receiverInfoLocationCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoTextCell *receiverInfoDetailLocationCell;

#pragma mark - Pay Info Cell
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *payInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradePayInfoSelectCell *payInfoWechatCell;

#pragma mark - 
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIPickerView *locationPicker;

@property (weak, nonatomic) IBOutlet UILabel *priceLabel;

#pragma mark - Init
- (id)initWithDict:(NSDictionary*)tradeDict;

#pragma mark - IBAction
- (IBAction)submitButtonPressed:(id)sender;
- (IBAction)receiverManageBtnPressed:(id)sender;

@end
