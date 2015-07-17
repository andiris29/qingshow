//
//  QSU10UserLocationListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSUserLocationTableViewCell.h"
@class QSU10ReceiverListViewController;

@protocol QSU10ReceiverListViewControllerDelegate <NSObject>

- (void)receiverListVc:(QSU10ReceiverListViewController*)vc didSelectReceiver:(NSDictionary*)receiver;

@end

@interface QSU10ReceiverListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, QSUserLocationTableViewCellDelegate, UIAlertViewDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) NSObject<QSU10ReceiverListViewControllerDelegate>* delegate;
- (instancetype)init;

@end
