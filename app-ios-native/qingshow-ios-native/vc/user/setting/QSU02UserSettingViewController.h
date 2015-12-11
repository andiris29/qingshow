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
#import "QSSinglePickerProvider.h"


@interface QSU02UserSettingViewController : UIViewController< UIActionSheetDelegate, UIImagePickerControllerDelegate, UIScrollViewDelegate, QSImageEditingViewControllerDelegate, QSU02AbstractTableViewCellDelegate, QSSinglePickerProviderDelegate, UINavigationControllerDelegate,UIAlertViewDelegate>

@property (weak, nonatomic) IBOutlet UIPickerView *picker;
@property (weak, nonatomic) IBOutlet UITableView* tableView;
@end
