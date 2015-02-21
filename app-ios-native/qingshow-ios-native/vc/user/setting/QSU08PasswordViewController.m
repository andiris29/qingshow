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

#define PAGE_ID @"U08 - 更改密码"

@interface QSU08PasswordViewController ()

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
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
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
    
    [self.delegate passwordViewController:self didSavingPassword:newPasswd needCurrentPassword:nowPasswd];
}

@end
