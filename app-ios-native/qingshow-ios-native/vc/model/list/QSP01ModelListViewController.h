//
//  QSModelListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSModelListTableViewCell.h"
#import "QSModelListTableViewProvider.h"

@interface QSP01ModelListViewController : UIViewController<QSModelListTableViewProviderDelegate>

@property (strong, nonatomic) IBOutlet UITableView* tableView;

@end
