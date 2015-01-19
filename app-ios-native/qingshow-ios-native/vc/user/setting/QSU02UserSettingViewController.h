//
//  QSU02UserSettingViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU04EmailViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSImageEditingViewController.h"

#define CODE_TYPE_GENDER @"gender"
#define CODE_TYPE_HAIR @"hairTypes"
#define GENDER_LIST @"男性", @"女性" 
#define HAIR_LIST @"所有", @"长发", @"超长发", @"中长发",@"短发"

@interface QSU02UserSettingViewController : UITableViewController <QSU04EmailViewControllerDelegate, QSU08PasswordViewControllerDelegate, UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIScrollViewDelegate, QSImageEditingViewControllerDelegate, UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UITextField *birthdayText;
@property (strong, nonatomic) IBOutlet UITableView *settingTableView;
@property (weak, nonatomic) IBOutlet UITextField *nameText;
@property (weak, nonatomic) IBOutlet UITextField *lengthText;
@property (weak, nonatomic) IBOutlet UITextField *weightText;
@property (weak, nonatomic) IBOutlet UIImageView *portraitImage;
@property (weak, nonatomic) IBOutlet UIImageView *backgroundImage;
@property (weak, nonatomic) IBOutlet UITextField *brandText;
@property (weak, nonatomic) IBOutlet UILabel *shoeSizeLabel;
@property (weak, nonatomic) IBOutlet UILabel *clothingSizeLabel;
@property (weak, nonatomic) IBOutlet UILabel *hairTypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *genderLabel;


- (IBAction)lengthEditingDidBegin:(id)sender;
- (IBAction)lengthEditingDidEnd:(id)sender;
- (IBAction)weightEditingDidBegin:(id)sender;
- (IBAction)weightEditingDidEnd:(id)sender;

@end
