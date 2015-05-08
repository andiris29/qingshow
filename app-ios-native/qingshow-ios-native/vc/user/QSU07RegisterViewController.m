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

#define PAGE_ID @"U07 - 注册"

@interface QSU07RegisterViewController ()
@property (weak, nonatomic) IBOutlet UITextField *accountText;
@property (weak, nonatomic) IBOutlet UITextField *passwdText;
@property (weak, nonatomic) IBOutlet UITextField *passwdCfmText;
@property (weak, nonatomic) IBOutlet UIButton *registerButton;

@end

@implementation QSU07RegisterViewController
{
    NSMutableArray *clothesArray;
    NSMutableArray *shoesArray;
}
- (void)configScrollView
{
    CGSize scrollViewSize = self.containerScrollView.bounds.size;
    CGSize contentSize = self.contentView.bounds.size;
    float height = scrollViewSize.height > contentSize.height ? scrollViewSize.height : contentSize.height;
    self.containerScrollView.contentSize = CGSizeMake(scrollViewSize.width, height + 20);
    [self.containerScrollView addSubview:self.contentView];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [self registerForKeyboardNotifications];
    [self configScrollView];
    // Do any additional setup after loading the view from its nib.
    
    // assign delegate
    self.accountText.delegate = self;
    self.passwdCfmText.delegate = self;
    self.passwdText.delegate = self;
    
    // Array alloc;
    clothesArray = [[NSMutableArray alloc]initWithCapacity:20];
    shoesArray = [[NSMutableArray alloc]initWithCapacity:20];
    
    // View全体
    self.view.backgroundColor=[UIColor colorWithRed:255.f/255.f green:255.f/255.f blue:255.f/255.f alpha:1.f];
    
    // Navibar
    self.navigationItem.title = @"注册";
    self.navigationItem.backBarButtonItem.title = @"";
    [self hideNaviBackBtnTitle];
    
    // Goto Login
    UIBarButtonItem *loginButton = [[UIBarButtonItem alloc] initWithTitle:@"登陆" style:UIBarButtonItemStyleDone target:self action:@selector(login)];
    self.navigationItem.rightBarButtonItem = loginButton;
    
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
    
    CALayer *layer = [self.shoeSizeLabel layer];
    CALayer *bottomBorder = [CALayer layer];
    bottomBorder.borderWidth = 1.0f;
    bottomBorder.frame = CGRectMake(0, layer.frame.size.height - 1, layer.frame.size.width, 1);
    [bottomBorder setBorderColor:[[UIColor colorWithRed:215.f/255.f green:220.f/255.f blue:224.f/255.f alpha:1.f] CGColor]];
    [layer addSublayer:bottomBorder];
    
    
    self.registerButton.layer.cornerRadius = self.registerButton.frame.size.height / 8;
    self.registerButton.layer.masksToBounds = YES;
    self.registerButton.backgroundColor = [UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f];
    
    self.gender = 1;
    self.clothingSize = 0;
    self.shoeSize = 34;
    
    //[self setSelectedStyleToPropButton:self.femaleButton];
//    [self setDefaultStyleToPropButon:self.maleButton];
//    [self setDefaultStyleToPropButon:self.femaleButton];
    //[self setUnSelectedStyleToPropButton:self.maleButton];
    [self.femaleButton setImage:[UIImage imageNamed:@"female_btn_hover"] forState:UIControlStateNormal];
    [self.maleButton setImage:[UIImage imageNamed:@"male_btn"] forState:UIControlStateNormal];
//    [self.femalLabel setTextColor:[UIColor colorWithRed:1.f green:1.f blue:1.f alpha:1.f]];
//    [self.maleLabel setTextColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
    
//    [clothesArray addObject:self.xxsButton];
//    [clothesArray addObject:self.xsButton];
//    [clothesArray addObject:self.sButton];
//    [clothesArray addObject:self.mButton];
//    [clothesArray addObject:self.lButton];
//    [clothesArray addObject:self.xlButton];
//    [clothesArray addObject:self.xxlButton];
//    [clothesArray addObject:self.xxxlButton];
//    
//    [shoesArray addObject:self.three4Button];
//    [shoesArray addObject:self.three5Button];
//    [shoesArray addObject:self.three6Button];
//    [shoesArray addObject:self.three7Button];
//    [shoesArray addObject:self.three8Button];
//    [shoesArray addObject:self.three9Button];
//    [shoesArray addObject:self.four0Button];
//    [shoesArray addObject:self.four1Button];
//    [shoesArray addObject:self.four2Button];
//    [shoesArray addObject:self.four3Button];
//    [shoesArray addObject:self.four4Button];
//    
//    [self setSizeStyleBySelectedSize:self.clothingSize buttonArray: clothesArray];
//    [self setSizeStyleBySelectedSize:self.shoeSize buttonArray:shoesArray];
//    
//    // tap Setting
//    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(resignOnTap:)];
//    [singleTap setNumberOfTapsRequired:1];
//    [singleTap setNumberOfTouchesRequired:1];
//    [self.contentView addGestureRecognizer:singleTap];
}
- (void)dealloc
{
    [self unregisterKeyboardNotifications];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
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
    [self.currentResponder resignFirstResponder];
}

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
    
//    if ([self checkEmail:account] != YES) {
//        [self showErrorHudWithText:@"请输入正确的邮件地址"];
//        return;
//    }
    
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
//        [self.navigationController popToViewController:[self.navigationController.viewControllers objectAtIndex:0] animated:YES];
        NSNumber *gender = [[NSNumber alloc] initWithLong:self.gender];
        NSNumber *clothingSize = [[NSNumber alloc] initWithLong:self.clothingSize];
        NSNumber *shoeSize = [[NSNumber alloc] initWithLong:self.shoeSize];
        [self updatePeopleEntityByEntity:@{@"gender": gender, @"clothingSize": clothingSize, @"shoeSize": shoeSize}];
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
    
    [SHARE_NW_ENGINE registerById:account Password:passwd onSuccess:successBloc onError:errorBlock];
}

# pragma mark - private
- (void)setDefaultStyleToPropButon:(UIButton *) button {
    button.layer.cornerRadius = button.frame.size.height / 8;
    button.layer.masksToBounds = YES;
    button.layer.borderWidth = 1.f;
    [button.layer setBorderColor:[[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f] CGColor]];
}

- (void)setSelectedStyleToPropButton:(UIButton *) button {
    [button setBackgroundColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
}

- (void)setUnSelectedStyleToPropButton:(UIButton *) button {
    [button setBackgroundColor:[UIColor whiteColor]];
    [button setTitleColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f] forState:UIControlStateNormal];
    [button.layer setBorderColor:[[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f] CGColor]];
}

- (void) setSizeStyleBySelectedSize:(NSInteger) selectSize buttonArray:(NSMutableArray *) array {
    for (UIButton *button in array) {
        NSInteger buttonTag = button.tag;
        if (buttonTag == selectSize) {
            [self setSelectedStyleToPropButton:button];
        } else {
            [self setUnSelectedStyleToPropButton:button];
        }
        [self setDefaultStyleToPropButon:button];
    }
}

// Update Peoples
- (void) updatePeopleEntityByEntity:(NSDictionary *)entity
{
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        if (metadata[@"error"] == nil && people != nil) {
            //[vc showSuccessHudWithText:@"更新成功"];
            [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
//            [self.navigationController popToViewController:[self.navigationController.viewControllers objectAtIndex:0] animated:YES];
            [self.navigationController popViewControllerAnimated:YES];
        } else {
            [self showErrorHudWithText:@"更新失败"];
        }
    };
    
    ErrorBlock error = ^(NSError *error) {
        if (error.userInfo[@"error"] != nil) {
            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
            if (errorCode != nil) {
            //    [vc showErrorHudWithText:@"更新失败，请确认输入的内容"];
            }
        } else {
            [self showErrorHudWithText:@"网络连接失败"];
        }
    };
    
    [SHARE_NW_ENGINE updatePeople:entity onSuccess:success onError:error];
}

- (IBAction)selectGender:(id)sender {
    [self resignOnTap:nil];
    self.gender = ((UIButton *)sender).tag;
    if (self.gender == 0) {
//        [self setSelectedStyleToPropButton:self.maleButton];
//        [self setUnSelectedStyleToPropButton:self.femaleButton];
//        [self.maleLabel setTextColor:[UIColor colorWithRed:1.f green:1.f blue:1.f alpha:1.f]];
//        [self.femalLabel setTextColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
        [self.femaleButton setImage:[UIImage imageNamed:@"female_btn"] forState:UIControlStateNormal];
        [self.maleButton setImage:[UIImage imageNamed:@"male_btn_hover"] forState:UIControlStateNormal];
    } else {
//        [self setSelectedStyleToPropButton:self.femaleButton];
//        [self setUnSelectedStyleToPropButton:self.maleButton];
//        [self.femalLabel setTextColor:[UIColor colorWithRed:1.f green:1.f blue:1.f alpha:1.f]];
//        [self.maleLabel setTextColor:[UIColor colorWithRed:128.f/255.f green:128.f/255.f blue:128.f/255.f alpha:1.f]];
        [self.femaleButton setImage:[UIImage imageNamed:@"female_btn_hover"] forState:UIControlStateNormal];
        [self.maleButton setImage:[UIImage imageNamed:@"male_btn"] forState:UIControlStateNormal];
    }
}

- (IBAction)selectClothingSize:(id)sender {
    [self resignOnTap:nil];
    self.clothingSize = ((UIButton *)sender).tag;
    [self setSizeStyleBySelectedSize:self.clothingSize buttonArray:clothesArray];
}


- (IBAction)selectShoeSize:(id)sender {
    [self resignOnTap:nil];
    self.shoeSize = ((UIButton *)sender).tag;
    [self setSizeStyleBySelectedSize:self.shoeSize buttonArray:shoesArray];
}

- (void)login{
    [self resignOnTap:nil];
    UIViewController *vc = [[QSU06LoginViewController alloc]initWithShowUserDetailAfterLogin:YES];
    //self.navigationController.navigationBarHidden = YES;
    [self.navigationController pushViewController:vc animated:YES];
}

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
@end
