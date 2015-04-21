//
//  QSS12TopicViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractRootViewController.h"
#import "QSTopicTableViewProvider.h"

@interface QSS12TopicViewController : QSAbstractRootViewController<QSTopicTableViewProviderDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end
