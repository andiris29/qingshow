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
    [datePicker setDate:[NSDate date]];
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
    NSDate *_birthday = [NSDate date];
    [self updateBirthDayLabel:_birthday];
}


- (void)updateBirthDayLabel:(NSDate *)birthDay {
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"yyyy/MM/dd"];
    self.birthdayText.text = [formatter stringFromDate:birthDay];
}

- (void)showDatePicker {
    
}

#pragma mark - Action
- (void)actionSave {
    
}

- (void)changeDate:(id)sender {
    UIDatePicker *datePicker = (UIDatePicker *)self.birthdayText.inputView;
    [self updateBirthDayLabel:datePicker.date];
}

@end
