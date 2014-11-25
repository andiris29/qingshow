//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController.h"
#import "QSU04EmailViewController.h"
#import "QSU05HairGenderTableViewController.h"
#import "QSU08PasswordViewController.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"

@interface QSU02UserSettingViewController ()
@end

@implementation QSU02UserSettingViewController

#pragma mark - private value

//UIDatePicker *datePicker;

#pragma mark - Method

- (void)awakeFromNib {
    self.portraitImage.layer.cornerRadius = self.portraitImage.frame.size.height / 2;
    self.portraitImage.layer.masksToBounds = YES;
    
    self.backgroundImage.layer.cornerRadius = self.backgroundImage.frame.size.height / 2;
    self.backgroundImage.layer.masksToBounds = YES;
}

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
    NSDictionary *people = [QSUserManager shareUserManager].userInfo;
    switch (indexPath.section) {
        case 0:
            // 选择section
            break;
        case 1:
            // 基本section
            if (indexPath.row == 1) {
                // GOTO Gender
                NSLog(@"GOTO Gender");
                QSU05HairGenderTableViewController *vc = [[QSU05HairGenderTableViewController alloc]
                                                          initWithNibName:@"QSU05HairGenderTableViewController"
                                                                   bundle:nil];
                
                if (people[@"gender"] != nil) {
                    NSArray *codes = [NSArray arrayWithObject:people[@"gender"]];
                    vc.selectCodes = codes;
                }
                vc.codeTable = GENDER_LIST;
                vc.delegate = self;
                vc.codeType = CODE_TYPE_GENDER;
                [self.navigationController pushViewController:vc animated:YES];
            } else if (indexPath.row == 2) {
                // 选择生日
            } else if (indexPath.row == 5) {
                // GOTO HairType
                NSLog(@"GOTO HairType");
                QSU05HairGenderTableViewController *vc = [[QSU05HairGenderTableViewController alloc]
                                                          initWithNibName:@"QSU05HairGenderTableViewController"
                                                                   bundle:nil];
                
                vc.codeTable = HAIR_LIST;
                vc.delegate = self;
                vc.codeType = CODE_TYPE_HAIR;
                if (people[@"hairTypes"] != nil) {
                    vc.selectCodes = people[@"hairTypes"];
                }
                [self.navigationController pushViewController:vc animated:YES];
            }
            
            break;
        case 2:
            // 其他section
            if (indexPath.row == 0) {
                // Change Password
                QSU08PasswordViewController *vc = [[QSU08PasswordViewController alloc]initWithNibName:@"QSU08PasswordViewController" bundle:nil];
                vc.delegate = self;
                [self.navigationController pushViewController:vc animated:YES];
            } else if (indexPath.row == 1) {
                // Change Email
                QSU04EmailViewController *vc = [[QSU04EmailViewController alloc]initWithNibName:@"QSU04EmailViewController" bundle:nil];
                vc.delegate = self;
                [self.navigationController pushViewController:vc animated:YES];
            } else {
                NSLog(@"Nothing");
            }
            break;
        default:
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == 2) {
        return 100.0f;
    }
    
    return [super tableView:tableView heightForFooterInSection:section];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    if (section == 2) {
        UIView *footerView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 100)];
        UIButton *addcharity=[UIButton buttonWithType:UIButtonTypeCustom];
        [addcharity setTitle:@"退出当前账号" forState:UIControlStateNormal];
        [addcharity addTarget:self action:@selector(actionLogout) forControlEvents:UIControlEventTouchUpInside];
        [addcharity setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];//set the color this is may be different for iOS 7
        [addcharity setBackgroundColor:[UIColor colorWithRed:251.f/255.f green:145.f/255.f blue:95.f/255.f alpha:1.f]];
        addcharity.frame=CGRectMake(10, 25, 300, 50); //set some large width to ur title
        [footerView addSubview:addcharity];
        return footerView;
    }
    return [super tableView:tableView viewForFooterInSection:section];
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
    self.lengthText.text = [(NSNumber *)people[@"height"] stringValue];
    self.weightText.text = [(NSNumber *)people[@"weight"] stringValue];
    if (people[@"birthtime"] == nil) {
        self.birthdayText.text = @"";
    } else {
        self.birthdayText.text = (NSString *)people[@"birthtime"];
    }
    
    if (people[@"portait"] != nil) {
        NSString *portaits = people[@"portait"];
        [self.portraitImage setImageFromURL:[NSURL URLWithString:portaits]];
    }
    if (people[@"background"] != nil) {
        NSString *background = people[@"background"];
        [self.backgroundImage setImageFromURL:[NSURL URLWithString:background]];
    }
}


- (void)updateBirthDayLabel:(NSDate *)birthDay {
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"yyyy/MM/dd"];
    self.birthdayText.text = [formatter stringFromDate:birthDay];
}

// Update Peoples
- (void) updatePeopleEntityViewController: (UIViewController *)vc byEntity:(NSDictionary *)entity {
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        if (metadata[@"error"] == nil && people != nil) {
            [vc showSuccessHudWithText:@"更新成功"];
            [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
            [vc.navigationController popToRootViewControllerAnimated:YES];
        } else {
            [vc showErrorHudWithText:@"更新失败"];
        }
    };
    
    ErrorBlock error = ^(NSError *error) {
        if (error.userInfo[@"error"] != nil) {
            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
            if (errorCode != nil) {
                [vc showErrorHudWithText:@"更新失败，请确认输入的内容"];
            }
        } else {
            [vc showErrorHudWithText:@"网络连接失败"];
        }
    };

    [SHARE_NW_ENGINE updatePeople:entity onSuccess:success onError:error];
}

#pragma mark - Action
- (void)actionSave {
    NSString *name = self.nameText.text;
    NSString *birthDay = self.birthdayText.text;
    NSString *length = self.lengthText.text;
    NSString *weight = self.weightText.text;
    
    [self updatePeopleEntityViewController:self byEntity:@{@"name": name, @"birthtime": birthDay, @"height": length, @"weight": weight}];
}

- (void)changeDate:(id)sender {
    UIDatePicker *datePicker = (UIDatePicker *)self.birthdayText.inputView;
    [self updateBirthDayLabel:datePicker.date];
}

- (void)actionLogout {
    NSLog(@"logout");
    VoidBlock succss = ^ {
        [QSUserManager shareUserManager].userInfo = nil;
        [QSUserManager shareUserManager].fIsLogined = NO;
        [self.navigationController popToRootViewControllerAnimated:YES];
    };
    [SHARE_NW_ENGINE logoutOnSucceed:succss onError:nil];
}

#pragma mark - Delegate
- (void)passwordViewController:(QSU08PasswordViewController *)vc didSavingPassword:(NSString *)newPassword needCurrentPassword:(NSString *)curPasswrod {
    [self updatePeopleEntityViewController:vc byEntity:@{@"password":newPassword, @"currentPassword": curPasswrod}];
}

- (void)emailViewController:(QSU04EmailViewController *)vc didSavingEmail:(NSString *)email {
    [self updatePeopleEntityViewController:vc byEntity:@{@"email": email}];
}

- (void)codeUpdateViewController:(QSU05HairGenderTableViewController *)vc forCodeType:(NSString *)codeType bySelectedCode:(NSArray *)codes {
    if ([codeType compare:CODE_TYPE_GENDER] == NSOrderedSame) {
        // Update Gender
        [self updatePeopleEntityViewController:vc byEntity:@{vc.codeType: codes[0]}];
    } else {
        // Update HairType
        NSMutableString *hairTypes = [[NSMutableString alloc]init];
        for (int i = 0; i < codes.count; i++) {
            [hairTypes appendString:[NSString stringWithFormat:@"%@", codes[i]]];
            if (i < (codes.count - 1)) {
                [hairTypes appendString:@","];
            }
        }
        [self updatePeopleEntityViewController:vc byEntity:@{vc.codeType: hairTypes}];
    }
}
@end
