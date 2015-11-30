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
#import "QSTotalPriceCell.h"
#import "QSLocationPickerProvider.h"

@protocol QSMenuProviderDelegate;

@interface QSU14CreateTradeViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, QSU10ReceiverListViewControllerDelegate, QSLocationPickerProviderDelegate, UIAlertViewDelegate>

#pragma mark - Item Info Cells
@property (strong, nonatomic) IBOutlet QSCreateTradeItemInfoTitleCell *itemInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *itemInfoQuantityCell;

#pragma mark - Receiver Info Cell
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *receiverInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoTextCell *receiverInfoNameCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoTextCell *receiverInfoPhoneCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoLocationCell *receiverInfoLocationCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeReceiverInfoTextCell *receiverInfoDetailLocationCell;

#pragma mark - Pay Info Cell
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *payInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradePayInfoSelectCell *payInfoWechatCell;
@property (strong, nonatomic) IBOutlet QSCreateTradePayInfoSelectCell *payInfoAlipayCell;
@property (strong, nonatomic) IBOutlet QSCreateTradePayInfoSelectCell *payInfoBankCell;


#pragma mark - Total Cell
@property (strong, nonatomic) IBOutlet QSTotalPriceCell *totalCell;

#pragma mark - 
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIPickerView *locationPicker;

@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;

#pragma mark - Init
- (id)initWithDict:(NSDictionary*)tradeDict;

#pragma mark - IBAction
- (IBAction)submitButtonPressed:(id)sender;
- (IBAction)receiverManageBtnPressed:(id)sender;

@end
