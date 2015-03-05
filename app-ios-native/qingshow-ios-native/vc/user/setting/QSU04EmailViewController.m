//
//  QSU04EmailViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/17.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU04EmailViewController.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"

#define PAGE_ID @"U04 - 邮箱设置"

@interface QSU04EmailViewController ()

@end

@implementation QSU04EmailViewController

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
    
    // Initialize Current Email Label
    self.nowEmailLabel.text = (NSString *) [QSUserManager shareUserManager].userInfo[@"userInfo"][@"email"];
    
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
    
    NSString *email = self.emailText.text;
    NSString *confirmEmail = self.confirmEmailText.text;
    
    if (email.length == 0) {
        [self showErrorHudWithText:@"请输入新地址"];
        return;
    }
    if ([confirmEmail compare:email] != NSOrderedSame) {
        [self showErrorHudWithText:@"两次地址不一致"];
        return;
    }
    
    [self.delegate emailViewController:self didSavingEmail:email];
}
@end
