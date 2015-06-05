//
//  QSU02UserSettingViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU04EmailViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSImageEditingViewController.h"

#import "QSRootContentViewController.h"
#import "QSU02AbstractTableViewCell.h"
@protocol QSMenuProviderDelegate;

#define CODE_TYPE_GENDER @"gender"
#define CODE_TYPE_HAIR @"hairTypes"

@interface QSU02UserSettingViewController : QSRootContentViewController<QSU04EmailViewControllerDelegate, QSU08PasswordViewControllerDelegate, UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIScrollViewDelegate, QSImageEditingViewControllerDelegate, UITextFieldDelegate, QSU02AbstractTableViewCellDelegate>

@property (weak, nonatomic) IBOutlet UITableView* tableView;
@end
