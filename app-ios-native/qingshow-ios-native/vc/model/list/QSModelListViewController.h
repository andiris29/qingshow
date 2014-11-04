//
//  QSModelListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSModelListTableViewCell.h"

@interface QSModelListViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, QSModelListTableViewCellDelegate>

@property (strong, nonatomic) IBOutlet UITableView* tableView;

@end
