//
//  QSU08PasswordViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/17.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU08PasswordViewController.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"

@interface QSU08PasswordViewController ()
@property (weak, nonatomic) IBOutlet UITextField *nowPasswdText;
@property (weak, nonatomic) IBOutlet UITextField *confirmPasswdText;
@property (weak, nonatomic) IBOutlet UITextField *passwdText;

@end

@implementation QSU08PasswordViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Initialize Navigation
    self.navigationItem.title = @"设置";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"保存"
                                                               style:UIBarButtonItemStylePlain
                                                              target:self
                                                              action:@selector(actionSave)];
    
    [[self navigationItem] setRightBarButtonItem:btnSave];
    
    // Initialize View
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Action
- (void)actionSave {
    
    NSString *nowPasswd = self.nowPasswdText.text;
    NSString *newPasswd = self.passwdText.text;
    NSString *confirmPasswd = self.confirmPasswdText.text;
    
    if (nowPasswd.length == 0) {
        [self showErrorHudWithText:@"请输入当前密码"];
        return;
    }
    if (newPasswd.length == 0) {
        [self showErrorHudWithText:@"请输入新密码"];
        return;
    }
    if ([confirmPasswd compare:newPasswd] != NSOrderedSame) {
        [self showErrorHudWithText:@"两次密码输入不一致"];
        return;
    }
    
}

@end
