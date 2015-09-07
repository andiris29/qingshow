//
//  QST01ShowTradeViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSRootContentViewController.h"
#import "QST01ShowTradeProvider.h"

@interface QST01ShowTradeViewController : QSRootContentViewController<QSAbstractScrollProviderDelegate>
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end
