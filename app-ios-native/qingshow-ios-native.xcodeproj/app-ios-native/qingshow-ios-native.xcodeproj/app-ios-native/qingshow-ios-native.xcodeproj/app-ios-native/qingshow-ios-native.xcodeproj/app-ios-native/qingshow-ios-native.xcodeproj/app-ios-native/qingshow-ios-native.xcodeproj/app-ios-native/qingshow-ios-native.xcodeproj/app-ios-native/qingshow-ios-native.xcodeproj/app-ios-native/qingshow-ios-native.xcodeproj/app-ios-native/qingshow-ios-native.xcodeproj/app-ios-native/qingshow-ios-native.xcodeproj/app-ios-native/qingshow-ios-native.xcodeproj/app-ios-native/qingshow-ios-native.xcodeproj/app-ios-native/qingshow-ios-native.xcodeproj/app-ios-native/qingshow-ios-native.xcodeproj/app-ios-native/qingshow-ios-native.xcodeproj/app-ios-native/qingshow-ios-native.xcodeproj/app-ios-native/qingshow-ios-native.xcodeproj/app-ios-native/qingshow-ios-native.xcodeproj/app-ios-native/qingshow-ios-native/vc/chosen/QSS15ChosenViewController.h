//
//  QSS15ChosenViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractRootViewController.h"
#import "QSBigImageTableViewProvider.h"

@interface QSS15ChosenViewController : QSAbstractRootViewController <QSBigImageTableViewProviderDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end
