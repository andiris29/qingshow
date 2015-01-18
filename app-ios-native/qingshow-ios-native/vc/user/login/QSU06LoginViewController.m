//
//  QSU06LoginViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/11.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "QSS01RootViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSU01UserDetailViewController.h"
#import "UIViewController+ShowHud.h"

@interface QSU06LoginViewController ()
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UITextField *userText;
@property (weak, nonatomic) IBOutlet UITextField *passwordText;

@property (assign, nonatomic) BOOL fSHowUserDetail;
@end

@implementation QSU06LoginViewController

#pragma mark - Init
- (id)initWithShowUserDetailAfterLogin:(BOOL)fShowUserDetail
{
    self = [super initWithNibName:@"QSU06LoginViewController" bundle:nil];
    if (self) {
        self.fSHowUserDetail = fShowUserDetail;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.userText.delegate = self;
    self.passwordText.delegate = self;
    
    // View全体
    self.view.backgroundColor=[UIColor colorWithRed:255.f/255.f green:255.f/255.f blue:255.f/255.f alpha:1.f];

    // Navibar
    self.navigationItem.title = @"登陆";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
//    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"注册"
//                                                               style:UIBarButtonItemStylePlain
//                                                              target:self
//                                                              action:@selector(gotoRegister)];
//    [[self navigationItem] setRightBarButtonItem:btnSave];
    
    for (UIView *subView in self.view.subviews) {
        if ([subView isKindOfClass:[UILabel class]]) {
            UILabel *label = (UILabel *)subView;
            if (label.tag == 99) {
                continue;
            }
            
            CALayer *layer = [label layer];
            CALayer *upperBorder = [CALayer layer];
            upperBorder.borderWidth=1.0f;
            upperBorder.frame = CGRectMake(0, 0, layer.frame.size.width, 1);
            [upperBorder setBorderColor:[[UIColor colorWithRed:215.f/255.f green:220.f/255.f blue:224.f/255.f alpha:1.f] CGColor]];
            [layer addSublayer:upperBorder];
        }
    }
    
    CALayer *layer = [self.passwordLabel layer];
    CALayer *bottomBorder = [CALayer layer];
    bottomBorder.borderWidth = 1.0f;
    bottomBorder.frame = CGRectMake(0, layer.frame.size.height - 1, layer.frame.size.width, 1);
    [bottomBorder setBorderColor:[[UIColor colorWithRed:215.f/255.f green:220.f/255.f blue:224.f/255.f alpha:1.f] CGColor]];
    [layer addSublayer:bottomBorder];

    // 登陆
//    self.loginButton.backgroundColor = [UIColor colorWithRed:252.f/255.f green:145.f/255.f blue:95.f/255.f alpha:1.f];
    self.loginButton.layer.cornerRadius = self.loginButton.frame.size.height / 8;
    self.loginButton.layer.masksToBounds = YES;
    
    // tap Setting
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(resignOnTap:)];
    [singleTap setNumberOfTapsRequired:1];
    [singleTap setNumberOfTouchesRequired:1];
    [self.view addGestureRecognizer:singleTap];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];

    NSMutableArray* a = [self.navigationController.viewControllers mutableCopy];
    if (![[a lastObject] isKindOfClass:[QSU07RegisterViewController class]]) {
        [a removeObject:self];
        self.navigationController.viewControllers = a;
    }
}

#pragma mark - UITextFieldDelegate
-(void)textFieldDidEndEditing:(UITextField *)textField {
    [textField resignFirstResponder];
}

-(void)textFieldDidBeginEditing:(UITextField *)textField {
    self.currentResponder = textField;
}

#pragma mark - Action
- (void)resignOnTap:(id)sender {
    [self.currentResponder resignFirstResponder];
}

- (IBAction)login:(id)sender {
    NSLog(@"login to qingshow");
    
    NSString *user = self.userText.text;
    NSString *passwd = self.passwordText.text;
    
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
            if (self.fSHowUserDetail) {
                [self.navigationController pushViewController:[[QSU01UserDetailViewController alloc] initWithCurrentUser] animated:YES];
            } else {
                [self.navigationController popViewControllerAnimated:YES];
            }

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
    UIViewController *vc = [[QSU07RegisterViewController alloc]initWithNibName:@"QSU07RegisterViewController" bundle:nil];
    [self.navigationController pushViewController:vc animated:YES];
}


@end
