//
//  QSU06LoginViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/11.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU06LoginViewController.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"

@interface QSU06LoginViewController ()
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UITextField *userText;
@property (weak, nonatomic) IBOutlet UITextField *passwordText;
@end

@implementation QSU06LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // View全体
    self.view.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];

    // Navibar
    self.navigationItem.title = @"登陆";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"注册"
                                                               style:UIBarButtonItemStylePlain
                                                              target:self
                                                              action:@selector(gotoRegister)];
    
    [[self navigationItem] setRightBarButtonItem:btnSave];


    // 登陆
    self.loginButton.backgroundColor = [UIColor colorWithRed:251.f/255.f green:145.f/255.f blue:95.f/255.f alpha:1.f];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    
}

#pragma mark - Action

- (IBAction)login:(id)sender {
    NSLog(@"login to qingshow");
    
    NSString *user = self.userText.text;
    NSString *passwd = self.userText.text;
    
    if (user.length == 0) {
        [self showErrorHudWithText:@"请输入账号"];
        return;
    }
    
    if (passwd.length == 0) {
        [self showErrorHudWithText:@"请输入密码"];
        return;
    }
    
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        if (metadata[@"error"] == nil && people != nil) {
            [self showSuccessHudWithText:@"登陆成功"];
            [self dismissViewControllerAnimated:YES completion:nil];
        } else {
            [self showErrorHudWithText:@"登陆失败"];
        }
    };
    
    ErrorBlock error = ^(NSError *error) {
        if (error.userInfo[@"error"] != nil) {
            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
            if (errorCode.longValue == 1001) {
                [self showErrorHudWithText:@"账号或者密码错误。"];
            }
        } else {
            [self showErrorHudWithText:@"网络连接失败"];
        }
    };
    
    [SHARE_NW_ENGINE loginWithName:user
                          password:passwd
                         onSucceed:success
                           onError:error];

}


#pragma mark - Callback

- (void)gotoRegister {
    NSLog(@"GOTO Register");
}


@end
