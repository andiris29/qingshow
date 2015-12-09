//
//  QSU09TradeListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSTradeListTableViewProvider.h"
#import "QSRootContentViewController.h"
#import "QSTradeListHeaderView.h"

@interface QSU09TradeListViewController : QSRootContentViewController <QSTradeListTableViewProviderDelegate,UIAlertViewDelegate,QSTradeListHeaderViewDelegate,UIAlertViewDelegate>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong,nonatomic) QSTradeListHeaderView *headerView;
@property (strong, nonatomic) QSTradeListTableViewProvider* provider;

- (instancetype)init;
@end
