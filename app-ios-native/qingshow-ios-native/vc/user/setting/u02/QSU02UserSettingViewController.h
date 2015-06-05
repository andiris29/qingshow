//
//  QSU02UserSettingViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QSU08PasswordViewController.h"
#import "QSImageEditingViewController.h"

#import "QSRootContentViewController.h"
#import "QSU02AbstractTableViewCell.h"


@interface QSU02UserSettingViewController : QSRootContentViewController< UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIScrollViewDelegate, QSImageEditingViewControllerDelegate, UITextFieldDelegate, QSU02AbstractTableViewCellDelegate>

@property (weak, nonatomic) IBOutlet UITableView* tableView;
@end
