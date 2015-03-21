//
//  QSCreateTradeViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/15/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSCreateTradeTableViewCellBase.h"
#import "QSCreateTradeColorAndSizeBaseTableViewCell.h"
#import "QSCreateTradeItemInfoColorCell.h"
#import "QSCreateTradeItemInfoSizeCell.h"
#import "QSCreateTradeItemInfoTitleCell.h"
#import "QSTotalPriceCell.h"

@interface QSS11CreateTradeViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, QSCreateTradeTableViewCellBaseDelegate>

#pragma mark - Item Info Cells
@property (strong, nonatomic) IBOutlet QSCreateTradeItemInfoTitleCell *itemInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeItemInfoColorCell *itemInfoColorCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeItemInfoSizeCell *itemInfoSizeCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *itemInfoQuantityCell;

#pragma mark - Receiver Info Cell
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *receiverInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *receiverInfoNameCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *receiverInfoPhoneCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *receiverInfoLocationCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *receiverInfoDetailLocationCell;

#pragma mark - Pay Info Cell
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *payInfoTitleCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *payInfoWechatCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *payInfoAllipayCell;
@property (strong, nonatomic) IBOutlet QSCreateTradeTableViewCellBase *payInfoBandCell;


#pragma mark - Total Cell
@property (strong, nonatomic) IBOutlet QSTotalPriceCell *totalCell;

#pragma mark - 
@property (weak, nonatomic) IBOutlet UITableView *tableView;


#pragma mark - Init
- (id)initWithDict:(NSDictionary*)dict;

#pragma mark - IBAction
- (IBAction)submitButtonPressed:(id)sender;

@end
