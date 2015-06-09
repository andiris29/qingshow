//
//  QSU07RegisterViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/14.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSS01RootViewController.h"
#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+Utility.h"
#import "UIViewController+QSExtension.h"
#import "QSNetworkKit.h"
#import "QSU13PersonalizeViewController.h"
#import "QSThirdPartLoginService.h"

#define PAGE_ID @"U07 - 注册"
#define w ([UIScreen mainScreen].bounds.size.width)
@interface QSU07RegisterViewController ()
@property (weak, nonatomic) IBOutlet UITextField *nickNameText;
@property (weak, nonatomic) IBOutlet UITextField *passwdText;
@property (weak, nonatomic) IBOutlet UITextField *passwdCfmText;
@property (weak, nonatomic) IBOutlet UITextField *mailAndPhoneText;
@property (weak, nonatomic) IBOutlet UIButton *registerButton;
@property (weak, nonatomic) IBOutlet UIButton *weixinButton;
@property (weak, nonatomic) IBOutlet UIButton *weiboButton;

@end

@implementation QSU07RegisterViewController

- (void)configScrollView
{
    
    CGSize scrollViewSize = self.containerScrollView.bounds.size;
    CGSize contentSize = self.contentView.bounds.size;
    float height = scrollViewSize.height > contentSize.height ? scrollViewSize.height : contentSize.height;
    self.containerScrollView.contentSize = CGSizeMake(scrollViewSize.width, height + 20);
    [self.view addSubview:self.contentView];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}
- (void)viewDidLoad {
    [super viewDidLoad];
   
    [self registerForKeyboardNotifications];
    [self configScrollView];
    [self.containerView addSubview:self.contentView];
    self.nickNameText.delegate = self;
    self.passwdCfmText.delegate = self;
    self.passwdText.delegate = self;
    self.mailAndPhoneText.delegate = self;
    [self.nickNameText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.passwdText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.passwdCfmText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.mailAndPhoneText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    self.nickNameText.tintColor = [UIColor whiteColor];
    self.passwdText.tintColor = [UIColor whiteColor];
    self.passwdCfmText.tintColor = [UIColor whiteColor];
    self.mailAndPhoneText.tintColor = [UIColor whiteColor];
    
    self.registerButton.layer.cornerRadius = self.registerButton.frame.size.height / 8;
    self.registerButton.layer.masksToBounds = YES;
    self.registerButton.backgroundColor = [UIColor colorWithWhite:1 alpha:0.2f];
  
    self.weixinButton.layer.cornerRadius = self.weixinButton.frame.size.height / 8;
    self.weixinButton.layer.masksToBounds = YES;
    [self.weixinButton setBackgroundColor:[UIColor colorWithWhite:1 alpha:0.2f]];
    self.weiboButton.layer.cornerRadius = self.weiboButton.frame.size.height / 8;
    [self.weiboButton setBackgroundColor:[UIColor colorWithWhite:1 alpha:0.2f]];
    self.weiboButton.layer.masksToBounds = YES;
    
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(resignOnTap:)];
    self.view.userInteractionEnabled = YES;
    [self.view addGestureRecognizer:ges];
    
    
}
- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    if (w == 414) {
        CGRect frame = self.navtextImageView.frame;
        frame.origin.x += 25;
        self.navtextImageView.frame = frame;
        CGRect frame01 = self.orLabel.frame;
        frame01.origin.x += 25;
        self.orLabel.frame = frame01;
        CGRect frame03 = self.leftLine.frame;
        frame03.size.width += 15;
        self.leftLine.frame = frame03;
        CGRect frame04 = self.rightLine.frame;
        frame04.origin.x +=20;
        frame04.size.width -= 10;
        self.rightLine.frame = frame04;
    }
    
}
- (void)dealloc
{
    [self unregisterKeyboardNotifications];
}
- (IBAction)login:(id)sender {
    [self resignOnTap:nil];
    UIViewController *vc = [[QSU06LoginViewController alloc]initWithPopToRootAfterLogin:YES];
    [self.navigationController pushViewController:vc animated:YES];
}
- (IBAction)back:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}
# pragma mark - UITextFieldDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.currentResponder = textField;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [textField resignFirstResponder];
}


# pragma mark - Action
- (void)resignOnTap:(id)iSender {
    [self hideKeyboard];
}
- (void)hideKeyboard
{
    for (UITextField* t in @[self.nickNameText, self.passwdText, self.passwdCfmText, self.mailAndPhoneText]) {
        [t resignFirstResponder];
    }
}

- (IBAction)registers:(id)sender {
    NSString *nickName = self.nickNameText.text;
    NSString *passwd = self.passwdText.text;
    NSString *passwdCfm = self.passwdCfmText.text;
    NSString *mailAndPhone = self.mailAndPhoneText.text;
    
    if (nickName.length == 0) {
        [self showErrorHudWithText:@"请输入昵称"];
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
    
    if([self checkEmail:mailAndPhone] != YES && [self checkMobile:mailAndPhone] != YES){
            [self showErrorHudWithText:@"请输入正确的邮箱或手机号"];
            return;
        }
    
    if ([passwd compare:passwdCfm] != NSOrderedSame) {
        [self showErrorHudWithText:@"密码不一致请重新输入"];
        return;
    }
    
    if ([self checkPasswd:passwd] != YES) {
        [self showErrorHudWithText:@"请输入6位以上英文或数字密码"];
        return;
    }
    
    EntitySuccessBlock successBloc = ^(NSDictionary *people, NSDictionary *meta) {
        [self showSuccessHudWithText:@"登陆成功"];
        //[self.navigationController popViewControllerAnimated:YES];
//        QSU13PersonalizeViewController *perliyVC = [[QSU13PersonalizeViewController alloc] init];
        [self.navigationController popToRootViewControllerAnimated:YES];
//        [self.navigationController pushViewController:perliyVC animated:YES];
    };
    
    ErrorBlock errorBlock = ^(NSError *error) {
        NSDictionary *userInfo = error.userInfo;
        NSNumber *errorCode = userInfo[@"error"];
        if (errorCode == nil) {
            [self showErrorHudWithText:@"网络连接失败"];
            return;
        }
        
        if (errorCode.longValue == 1010) {
            [self showErrorHudWithText:@"该账号已被注册"];
            return;
        }
    };
    [SHARE_NW_ENGINE registerByNickname:nickName Password:passwd Id:mailAndPhone onSucceessd:successBloc onErrer:errorBlock];

}



//// Update Peoples
//- (void) updatePeopleEntityByEntity:(NSDictionary *)entity
//{
//    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
//        if (metadata[@"error"] == nil && people != nil) {
//            //[vc showSuccessHudWithText:@"更新成功"];
//            [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
////            [self.navigationController popToViewController:[self.navigationController.viewControllers objectAtIndex:0] animated:YES];
//            [self.navigationController popViewControllerAnimated:YES];
//        } else {
//            [self showErrorHudWithText:@"更新失败"];
//        }
//    };
//    
//    ErrorBlock error = ^(NSError *error) {
//        if (error.userInfo[@"error"] != nil) {
//            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
//            if (errorCode != nil) {
//            //    [vc showErrorHudWithText:@"更新失败，请确认输入的内容"];
//            }
//        } else {
//            [self showErrorHudWithText:@"网络连接失败"];
//        }
//    };
//    
//    [SHARE_NW_ENGINE updatePeople:entity onSuccess:success onError:error];
//}



#pragma mark - Keyboard
- (void)registerForKeyboardNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter]  addObserver:self selector:@selector(keyboardWasHidden:) name:UIKeyboardWillHideNotification object:nil];
}
- (void)unregisterKeyboardNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)keyboardWasShown:(NSNotification *) notif
{
    NSDictionary *info = [notif userInfo];
    NSValue *value = [info objectForKey:UIKeyboardFrameBeginUserInfoKey];
    CGSize keyboardSize = [value CGRectValue].size;
    
    self.containerScrollView.contentInset = UIEdgeInsetsMake(0, 0, keyboardSize.height, 0);
}
- (void)keyboardWasHidden:(NSNotification *) notif
{
    [UIView animateWithDuration:0.3f animations:^{
        self.containerScrollView.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
    }];
}

- (IBAction)loginWechatBtnPressed:(id)sender {
    [self hideKeyboard];
    [[QSThirdPartLoginService getInstance] loginWithWechatOnSuccees:^{
        [self.navigationController popToRootViewControllerAnimated:YES];
    } onError:^(NSError *error) {
        [self showErrorHudWithText:@"微信登陆失败"];
    }];
}

- (IBAction)loginWeiboBtnPressed:(id)sender {
    [self hideKeyboard];
    [[QSThirdPartLoginService getInstance] loginWithWeiboOnSuccees:^{
        [self.navigationController popToRootViewControllerAnimated:YES];
    } onError:^(NSError *error) {
        [self showErrorHudWithText:@"微博登陆失败"];
    }];
}
@end
