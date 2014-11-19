//
//  QSU07RegisterViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/14.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU07RegisterViewController.h"
#import "QSS01RootViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+Utility.h"
#import "QSNetworkEngine.h"

@interface QSU07RegisterViewController ()
@property (weak, nonatomic) IBOutlet UITextField *accountText;
@property (weak, nonatomic) IBOutlet UITextField *passwdText;
@property (weak, nonatomic) IBOutlet UITextField *passwdCfmText;
@property (weak, nonatomic) IBOutlet UIButton *registerButton;

@end

@implementation QSU07RegisterViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // View全体
    self.view.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];
    
    // Navibar
    self.navigationItem.title = @"注册";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
//    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"注册"
//                                                               style:UIBarButtonItemStylePlain
//                                                              target:self
//                                                              action:@selector(gotoRegister)];
//    
//    [[self navigationItem] setRightBarButtonItem:btnSave];
//    
//    
    // 登陆
    self.registerButton.backgroundColor = [UIColor colorWithRed:251.f/255.f green:145.f/255.f blue:95.f/255.f alpha:1.f];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

# pragma mark - Action

- (IBAction)register:(id)sender {

    NSString *account = self.accountText.text;
    NSString *passwd = self.passwdText.text;
    NSString *passwdCfm = self.passwdCfmText.text;
    
    if (account.length == 0) {
        [self showErrorHudWithText:@"请输入账号"];
        return;
    }
    
    if (passwd.length == 0) {
        [self showErrorHudWithText:@"请输入密码"];
        return;
    }
    
    if (passwdCfm.length == 0) {
        [self showErrorHudWithText:@"请再次输入密码"];
        return;
    }
    
    if ([self checkEmail:account] != YES) {
        [self showErrorHudWithText:@"请输入正确的邮件地址"];
        return;
    }
    
    if ([self checkPasswd:passwd] != YES) {
        [self showErrorHudWithText:@"请输入8-12位的英文或数字"];
        return;
    }
    
    if ([passwd compare:passwdCfm] != NSOrderedSame) {
        [self showErrorHudWithText:@"密码不一致请重新输入"];
        return;
    }
    
    EntitySuccessBlock successBloc = ^(NSDictionary *people, NSDictionary *meta) {
        [self showSuccessHudWithText:@"登陆成功"];
        [self dismissViewControllerAnimated:YES completion:nil];
//        UIViewController *vc = [[QSS01RootViewController alloc]initWithNibName:@"QSS01RootViewController" bundle:nil];
//        [self.navigationController pushViewController:vc animated:YES];
        [self.navigationController popToViewController:[self.navigationController.viewControllers objectAtIndex:0] animated:YES];
    };
    
    ErrorBlock errorBlock = ^(NSError *error) {
        NSDictionary *userInfo = error.userInfo;
        NSNumber *errorCode = userInfo[@"error"];
        if (errorCode == nil) {
            [self showErrorHudWithText:@"网络连接失败"];
            return;
        }
        
        if (errorCode.longValue == 1010) {
            [self showErrorHudWithText:@"该邮箱地址已被注册"];
            return;
        }
    };
    
    [SHARE_NW_ENGINE registerByMail:account Password:passwd onSuccess:successBloc onError:errorBlock];
}

# pragma mark - private



@end
