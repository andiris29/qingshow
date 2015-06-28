//
//  QSAbstractRootViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>
#import "QSAbstractRootViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "UIView+ScreenShot.h"
#import "UIViewController+QSExtension.h"
#import "QSU02UserSettingViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "QSU01UserDetailViewController.h"
#import "QSAppDelegate.h"
#import "QSUserManager.h"

#import "QSU13PersonalizeViewController.h"

#import "QSPeopleUtil.h"
#import "QSS21CategorySelectorVC.h"

#define kWelcomePageVersionKey @"kWelcomePageVersionKey"


@interface QSAbstractRootViewController ()


@property (assign, nonatomic) BOOL fIsShowMenu;
@property (assign, nonatomic) BOOL fIsFirstLoad;



@property (strong, nonatomic) QSG02WelcomeViewController* welcomeVc;

@end

@implementation QSAbstractRootViewController


#pragma mark - Init
- (instancetype)init {
    self = [super init];
    if (self) {
        self.hasFetchUserLogin = NO;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self configNavBar];
    QSRootMenuView* menuView = [QSRootMenuView generateView];
    
    //    [self.navigationController.view addSubview:menuView];
    [((QSAppDelegate*)[UIApplication sharedApplication].delegate).window addSubview:menuView];
    self.menuView = menuView;
    self.fIsShowMenu = NO;
    menuView.delegate = self;
    
    [self hideNaviBackBtnTitle];
    
    if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    }
    
    self.fIsFirstLoad = YES;
    
    self.welcomeVc = [[QSG02WelcomeViewController alloc] init];
    self.welcomeVc.delegate = self;
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    self.menuView.hidden = !self.fIsShowMenu;
    
    if (self.fIsFirstLoad) {
        self.fIsFirstLoad = NO;
        
        NSUserDefaults* userDefault = [NSUserDefaults standardUserDefaults];
//        userDefault valueFor
        NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
        if (![[userDefault valueForKey:kWelcomePageVersionKey] isEqualToString:version]) {
            [self.navigationController.view addSubview:self.welcomeVc.view];
            [userDefault setValue:version forKey:kWelcomePageVersionKey];
            [userDefault synchronize];
        }
    }
    
    if (self.hasFetchUserLogin) {
        [self handleCurrentUser];
    }

}

- (void)handleCurrentUser
{
    NSDictionary* userInfo = [QSUserManager shareUserManager].userInfo;
    if (userInfo && ![QSPeopleUtil hasPersonalizeData:userInfo]) {
        [self.navigationController pushViewController:[[QSU13PersonalizeViewController alloc] init] animated:YES];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideMenu];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Configure View
- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    titleImageView.userInteractionEnabled = YES;
    
    self.navigationItem.titleView = titleImageView;
}

#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuViewDidTapBlankView
{
    [self hideMenu];
}
- (void)rootMenuItemPressedType:(QSRootMenuItemType)type oldType:(QSRootMenuItemType)oldType
{
    [self hideMenu];
}

#pragma mark - IBAction
- (void)menuButtonPressed
{

    __weak QSAbstractRootViewController* weakSelf = self;
    if (self.fIsShowMenu)
    {
        [self.menuView hideMenuAnimationComple:^{
            weakSelf.menuView.hidden = YES;
        }];
    }
    else
    {
        weakSelf.menuView.hidden = NO;
        [self.menuView showMenuAnimationComple:^{
        }];
    }
    self.fIsShowMenu = !self.fIsShowMenu;
}

- (void)hideMenu
{
    if (self.fIsShowMenu)
    {
        __weak QSAbstractRootViewController* weakSelf = self;
        [self.menuView hideMenuAnimationComple:^{
            weakSelf.menuView.hidden = YES;
        }];
        self.fIsShowMenu = NO;
    }
}

#pragma mark - QSG02WelcomeViewControllerDelegate
- (void)dismissWelcomePage:(QSG02WelcomeViewController*)vc
{
    [UIView animateWithDuration:0.5f animations:^{
        vc.view.alpha = 0.f;
    } completion:^(BOOL finished) {
        [vc.view removeFromSuperview];
    }];
}
#pragma mark - QSMenuProviderDelegate
- (void)didClickMenuBtn
{
    [self menuButtonPressed];
}
- (void)showRegisterVc
{
}
@end
