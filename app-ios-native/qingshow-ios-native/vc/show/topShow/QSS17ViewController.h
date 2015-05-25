//
//  QSS17ViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSTableViewBasicProvider.h"
#import "QSShareViewController.h"
#import "QSRootContentViewController.h"
#import "QSS17TavleViewProvider.h"

@interface QSS17ViewController : QSRootContentViewController<QSS17ProviderDelegate>

@property (weak, nonatomic) IBOutlet UITableView *topShowTableView;


@end

