//
//  QSS02FashionViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/21/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBigImageTableViewProvider.h"
#import "QSShareViewController.h"

@interface QSS08PreviewViewController : UIViewController <QSBigImageTableViewProviderDelegate, QSShareViewControllerDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end
