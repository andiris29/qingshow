//
//  QSU02UserSettingViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSU04EmailViewController.h"
#import "QSU05HairGenderTableViewController.h"
#import "QSU08PasswordViewController.h"

#define CODE_TYPE_GENDER @"gender"
#define CODE_TYPE_HAIR @"hairTypes"
#define GENDER_LIST [NSArray arrayWithObjects:@"男性", @"女性", nil]
#define HAIR_LIST [NSArray arrayWithObjects:@"所有", @"长发", @"超长发", @"中长发",@"短发", nil]

@interface QSU02UserSettingViewController : UITableViewController <QSU04EmailViewControllerDelegate, CodeUpdateViewControllerDelegate, QSU08PasswordViewControllerDelegate, UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (weak, nonatomic) IBOutlet UITextField *birthdayText;
@property (strong, nonatomic) IBOutlet UITableView *settingTableView;
@property (weak, nonatomic) IBOutlet UITextField *nameText;
@property (weak, nonatomic) IBOutlet UITextField *lengthText;
@property (weak, nonatomic) IBOutlet UITextField *weightText;
@property (weak, nonatomic) IBOutlet UIImageView *portraitImage;
@property (weak, nonatomic) IBOutlet UIImageView *backgroundImage;
- (IBAction)lengthEditingDidBegin:(id)sender;
- (IBAction)lengthEditingDidEnd:(id)sender;
- (IBAction)weightEditingDidBegin:(id)sender;
- (IBAction)weightEditingDidEnd:(id)sender;

@end
