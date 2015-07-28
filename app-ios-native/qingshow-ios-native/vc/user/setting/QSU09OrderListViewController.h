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

@interface QSU09OrderListViewController : QSRootContentViewController <QSOrderListTableViewProviderDelegate,UIAlertViewDelegate>
@property (weak, nonatomic) IBOutlet UITableView *tableView;

- (instancetype)init;

@end
