//
//  QSItemBuyViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSItemBuyViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

- (instancetype)initWithItem:(NSDictionary*)itemDict promoterId:(NSString*)promoterId;

@end
