//
//  QSU10UserLocationListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSUserLocationTableViewCell.h"

@interface QSU10UserLocationListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, QSUserLocationTableViewCellDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

- (instancetype)init;

@end
