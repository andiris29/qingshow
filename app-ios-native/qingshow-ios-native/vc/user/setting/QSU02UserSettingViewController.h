//
//  QSU02UserSettingViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU08PasswordViewController.h"

@interface QSU02UserSettingViewController : UITableViewController <QSU08PasswordViewControllerDelegate>

@property (weak, nonatomic) IBOutlet UITextField *birthdayText;
@property (strong, nonatomic) IBOutlet UITableView *settingTableView;
@property (weak, nonatomic) IBOutlet UITextField *nameText;
@property (weak, nonatomic) IBOutlet UITextField *lengthText;
@property (weak, nonatomic) IBOutlet UITextField *weightText;

@end
