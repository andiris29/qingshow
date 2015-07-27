//
//  QSCreateTradeViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/15/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSCreateTradeTableViewCellBase.h"
#import "QSCreateTradeItemInfoColorCell.h"
#import "QSCreateTradeClothSizeCell.h"
#import "QSCreateTradeShoeSizeCell.h"

#import "QSCreateTradeItemInfoTitleCell.h"
#import "QSU10ReceiverListViewController.h"
#import "QSCreateTradeReceiverInfoTextCell.h"
#import "QSCreateTradeReceiverInfoLocationCell.h"
#import "QSCreateTradePayInfoSelectCell.h"
#import "QSTotalPriceCell.h"
#import "QSLocationPickerProvider.h"

@interface QSS11CreateTradeViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, QSCreateTradeTableViewCellBaseDelegate, QSU10ReceiverListViewControllerDelegate, QSLocationPickerProviderDelegate, UIAlertViewDelegate>

#pragma mark - Item Info Cells
@property (strong, nonatomic) IBOutlet QSCreateTradeItemInfoTitleCell *itemInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeItemInfoColorCell *itemInfoColorCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeClothSizeCell* clothSizeCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeShoeSizeCell* shoeSizeCell;
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


#pragma mark - Init
- (id)initWithDict:(NSDictionary*)dict;

#pragma mark - IBAction
- (IBAction)submitButtonPressed:(id)sender;
- (IBAction)receiverManageBtnPressed:(id)sender;

@end
