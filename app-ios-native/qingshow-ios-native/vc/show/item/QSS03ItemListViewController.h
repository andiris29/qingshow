//
//  QSS03ItemListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSS03ItemListViewControllerDelegate <NSObject>

- (void)didClickCloseBtn;

@end

@interface QSS03ItemListViewController : UIViewController<UITableViewDataSource, UITableViewDelegate>

//@property (weak, nonatomic) IBOutlet UIImageView *bgImageView;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) NSObject<QSS03ItemListViewControllerDelegate>* delegate;


- (id)initWithShow:(NSDictionary*)showDict;

- (IBAction)closeBtnPressed:(id)sender;

@end
