//
//  QSS15TopicViewController.h
//  qingshow-ios-native
//
//  Created by ching show on 15/4/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSAbstractRootViewController.h"
#import "QSTopicTableViewProvider.h"
@interface QSS15TopicViewController : QSAbstractRootViewController<QSTopicTableViewProviderDelegate>
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end
