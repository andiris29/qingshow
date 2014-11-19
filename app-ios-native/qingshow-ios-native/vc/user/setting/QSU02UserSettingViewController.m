//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController.h"
#import "QSU04EmailViewController.h"
#import "QSU05HairTypeGenderViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"

#warning 从UserManager取当前登陆用户信息

@interface QSU02UserSettingViewController ()
@property (weak, nonatomic) IBOutlet UITextField *birthdayText;
@property (strong, nonatomic) IBOutlet UITableView *settingTableView;
@property (weak, nonatomic) IBOutlet UITextField *nameText;
@property (weak, nonatomic) IBOutlet UITextField *lengthText;
@property (weak, nonatomic) IBOutlet UITextField *weightText;
@end

@implementation QSU02UserSettingViewController

#pragma mark - private value

//UIDatePicker *datePicker;

#pragma mark - Method

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initNavigation];
    [self loadUserSetting];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableViewDataSource


//- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
//    return [super tableView:tableView cellForRowAtIndexPath:indexPath];
//}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            // 选择section
            break;
        case 1:
            // 基本section
            if (indexPath.row == 1) {
                // GOTO Gender
                NSLog(@"GOTO Gender");
            } else if (indexPath.row == 2) {
                // 选择生日
            } else if (indexPath.row == 5) {
                // GOTO HairType
                NSLog(@"GOTO HairType");
            }
            
            break;
        case 2:
            // 其他section
            if (indexPath.row == 0) {
                // Change Password
                QSU08PasswordViewController *vc = [[QSU08PasswordViewController alloc]initWithNibName:@"QSU08PasswordViewController" bundle:nil];
                [self.navigationController pushViewController:vc animated:YES];
            } else if (indexPath.row == 1) {
                // Change Email
                QSU04EmailViewController *vc = [[QSU04EmailViewController alloc]initWithNibName:@"QSU04EmailViewController" bundle:nil];
                [self.navigationController pushViewController:vc animated:YES];
            } else {
                NSLog(@"Nothing");
            }
            break;
        default:
            break;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    UIDatePicker *datePicker = [[UIDatePicker alloc] init];
    datePicker.datePickerMode = UIDatePickerModeDate;
    [datePicker setLocale:[NSLocale currentLocale]];
    if (self.birthdayText.text.length == 0) {
        [datePicker setDate:[NSDate date]];
    } else {
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setDateFormat:@"yyyy/MM/dd"];
        NSDate *date = [dateFormat dateFromString:self.birthdayText.text];
        [datePicker setDate:date];
    }
    [datePicker addTarget:self action:@selector(changeDate:) forControlEvents:UIControlEventValueChanged];
    
    if (textField.tag == 11) {
        self.birthdayText.inputView = datePicker;
        [self updateBirthDayLabel:datePicker.date];
    }

}

#pragma mark - Private

- (void)initNavigation {
    NSLog(@"initNavigation");
    self.navigationItem.title = @"设置";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"保存"
                                                               style:UIBarButtonItemStylePlain
                                                              target:self
                                                              action:@selector(actionSave)];
    
    [[self navigationItem] setRightBarButtonItem:btnSave];
}

- (void)loadUserSetting {
    
    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
    self.nameText.text = (NSString *)people[@"name"];
    self.lengthText.text = (NSString *)people[@"height"];
    self.nameText.text = (NSString *)people[@"width"];
    if (people[@"birthtime"] == nil) {
        self.birthdayText.text = @"";
    } else {
        self.birthdayText.text = (NSString *)people[@"birthtime"];
    }
}


- (void)updateBirthDayLabel:(NSDate *)birthDay {
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"yyyy/MM/dd"];
    self.birthdayText.text = [formatter stringFromDate:birthDay];
}

#pragma mark - Action
- (void)actionSave {
    NSString *name = self.nameText.text;
    NSString *birthDay = self.birthdayText.text;
    NSString *length = self.lengthText.text;
    NSString *weight = self.weightText.text;
    
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        if (metadata[@"error"] == nil && people != nil) {
            [self showSuccessHudWithText:@"更新成功"];
            [self.navigationController popToRootViewControllerAnimated:YES];
        } else {
            [self showErrorHudWithText:@"更新失败"];
        }
    };
    
    ErrorBlock error = ^(NSError *error) {
        if (error.userInfo[@"error"] != nil) {
            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
            if (errorCode != nil) {
                [self showErrorHudWithText:@"更新失败，请确认输入的内容"];
            }
        } else {
            [self showErrorHudWithText:@"网络连接失败"];
        }
    };

    [SHARE_NW_ENGINE updatePeople:@{@"name": name, @"birthtime": birthDay, @"height": length, @"weight": weight}
                        onSuccess:success
                          onError:error];
}

- (void)changeDate:(id)sender {
    UIDatePicker *datePicker = (UIDatePicker *)self.birthdayText.inputView;
    [self updateBirthDayLabel:datePicker.date];
}

@end
