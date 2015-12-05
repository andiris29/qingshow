//
//  QSItemBuyViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS11ItemBuyViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (weak, nonatomic) IBOutlet UIButton *discountInfoBtn;
@property (weak, nonatomic) IBOutlet UIButton *buyBtn;


@property (strong, nonatomic) IBOutlet UIView *btnContainer;
- (instancetype)initWithItem:(NSDictionary*)itemDict promoterId:(NSString*)promoterId;

@end
