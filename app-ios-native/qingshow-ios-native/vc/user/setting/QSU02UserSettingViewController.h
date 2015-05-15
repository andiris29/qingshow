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


@property (strong, nonatomic) IBOutlet UITableView *settingTableView;
@property (weak, nonatomic) IBOutlet UITextField *ageText;
//昵称
@property (weak, nonatomic) IBOutlet UITextField *nameText;
//身高
@property (weak, nonatomic) IBOutlet UITextField *lengthText;
//体重
@property (weak, nonatomic) IBOutlet UITextField *weightText;
//体型
@property (weak, nonatomic) IBOutlet UITextField *bodyTpye;
//穿衣风格
@property (weak, nonatomic) IBOutlet UITextField *dressTpye;
//搭配效果
@property (weak, nonatomic) IBOutlet UITextField *expectationTpye;
@property (weak, nonatomic) IBOutlet UIImageView *portraitImage;
@property (weak, nonatomic) IBOutlet UIImageView *backgroundImage;


- (IBAction)lengthEditingDidBegin:(id)sender;
- (IBAction)lengthEditingDidEnd:(id)sender;
- (IBAction)weightEditingDidBegin:(id)sender;
- (IBAction)weightEditingDidEnd:(id)sender;

@end
