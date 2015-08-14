//
//  QSU09OrderListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSOrderListTableViewProvider.h"
#import "QSRootContentViewController.h"
#import "QSOrderListHeaderView.h"

@interface QSU09OrderListViewController : QSRootContentViewController <QSOrderListTableViewProviderDelegate,UIAlertViewDelegate,QSOrderListHeaderViewDelegate,UIAlertViewDelegate>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong,nonatomic) QSOrderListHeaderView *headerView;
- (instancetype)init;

@end
