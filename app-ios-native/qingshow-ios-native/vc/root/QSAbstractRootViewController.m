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
    
    [self _initContainerViews];
    [self _initMenuView];
    
    self.fIsFirstLoad = YES;
    
    self.welcomeVc = [[QSG02WelcomeViewController alloc] init];
    self.welcomeVc.delegate = self;
#warning Somewhere to remove child view controller
    [self addChildViewController:self.welcomeVc];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.menuView.hidden = !self.fIsShowMenu;
    
    if (self.fIsFirstLoad) {
        self.fIsFirstLoad = NO;
        
        NSUserDefaults* userDefault = [NSUserDefaults standardUserDefaults];
//        userDefault valueFor
        NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
        if (![[userDefault valueForKey:kWelcomePageVersionKey] isEqualToString:version]) {
            [self.contentContainerView addSubview:self.welcomeVc.view];
            [userDefault setValue:version forKey:kWelcomePageVersionKey];
            [userDefault synchronize];
        }
    }
    
    if (self.hasFetchUserLogin) {
        [self handleCurrentUser];
    }

}


- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideMenu];
    
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    [self _layoutContainerViews];
    self.menuView.frame = self.menuContainerView.bounds;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark -

- (void)handleCurrentUser
{
    NSDictionary* userInfo = [QSUserManager shareUserManager].userInfo;
    if (userInfo && [QSPeopleUtil getPeopleRole:userInfo] == QSPeopleRoleUser && ![QSPeopleUtil hasPersonalizeData:userInfo]) {
#warning MOVE TO POPOVER CONTAINER
        [self presentViewController:[[QSU13PersonalizeViewController alloc] init] animated:YES completion:nil];
    }
}
#pragma mark - Configure View
- (void)_initContainerViews {
    self.contentContainerView = [[UIView alloc] init];
    [self.view addSubview:self.contentContainerView];
    self.contentContainerView.hidden = NO;
    self.menuContainerView = [[UIView alloc] init];
    [self.view addSubview:self.menuContainerView];
    
    self.menuContainerView.hidden = YES;
    self.popOverContainerView = [[UIView alloc] init];
    [self.view addSubview:self.popOverContainerView];
    self.popOverContainerView.hidden = YES;
}
- (void)_initMenuView {
    QSRootMenuView* menuView = [QSRootMenuView generateView];
    [self.menuContainerView addSubview:menuView];
    self.menuView = menuView;
    self.fIsShowMenu = NO;
    menuView.delegate = self;

}

#pragma mark Layout

- (void)_layoutContainerViews {
    CGRect r = self.view.bounds;
    self.contentContainerView.frame = r;
    self.menuContainerView.frame = r;
    self.popOverContainerView.frame = r;
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
            weakSelf.menuContainerView.hidden = YES;
        }];
    }
    else
    {

        [self.menuView showMenuAnimationComple:^{
        }];
        weakSelf.menuView.hidden = NO;
        weakSelf.menuContainerView.hidden = NO;
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
            weakSelf.menuContainerView.hidden = YES;
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
- (UIViewController*)showRegisterVc {
    return nil;
}
- (UIViewController*)showDefaultVc {
    return nil;
}
- (UIViewController*)showGuestVc {
    return nil;
}
- (UIViewController*)triggerToShowVc:(QSRootMenuItemType)type {
    return nil;
}

#pragma mark - Rotation
-(BOOL)shouldAutorotate {
    return YES;
}
-(NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationPortrait;
}
@end
