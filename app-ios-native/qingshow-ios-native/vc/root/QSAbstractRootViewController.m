//
//  QSAbstractRootViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractRootViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "UIView+ScreenShot.h"
#import "UIViewController+QSExtension.h"
#import "QSU02UserSettingViewController.h"
#import "QSS02ItemFeedingViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "QSS08PreviewViewController.h"
#import "QSU01UserDetailViewController.h"
#import "QSS02CategoryViewController.h"
#import "QSAppDelegate.h"
#import "QSUserManager.h"
#import "QSS08FashionViewController.h"
#import "QSS02ItemFeedingViewController.h"

#import "QSS17TopShowsViewController.h"

@interface QSAbstractRootViewController ()

@property (strong, nonatomic) QSRootMenuView* menuView;
@property (assign, nonatomic) BOOL fIsShowMenu;
@property (assign, nonatomic) BOOL fIsFirstLoad;

@property (strong, nonatomic) UIBarButtonItem* menuBtn;
@property (strong, nonatomic) UIBarButtonItem* menuBtnNew;

@end

@implementation QSAbstractRootViewController
#pragma mark - Getter And Setter
- (UIBarButtonItem*)menuBtn {
    if (!_menuBtn) {
        _menuBtn = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
    }
    return _menuBtn;
}
- (UIBarButtonItem*)menuBtnNew {
    if (!_menuBtnNew) {
        UIImageView* navBtnMenuNewImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu_new"]];
        UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(menuButtonPressed)];
        navBtnMenuNewImageView.userInteractionEnabled = YES;
        [navBtnMenuNewImageView addGestureRecognizer:ges];
        _menuBtnNew = [[UIBarButtonItem alloc] initWithCustomView:navBtnMenuNewImageView];;
    }
    return _menuBtnNew;
}


#pragma mark - Init
- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
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
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    self.menuView.hidden = !self.fIsShowMenu;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideMenu];
    
    if (self.fIsFirstLoad) {
        self.fIsFirstLoad = NO;
    }
    
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
    UITapGestureRecognizer* tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapRootTitle)];
    tapGes.numberOfTapsRequired = 5;
    [titleImageView addGestureRecognizer:tapGes];
    
    self.navigationItem.titleView = titleImageView;
    
    
#warning TODO add date to judge new menu btn
    NSDate* lastClickMenuDate = [QSUserManager shareUserManager].lastClickMenuDate;
    if (!lastClickMenuDate || [[NSDate date] timeIntervalSinceDate:lastClickMenuDate] >= 24 * 60 * 60) {
        self.navigationItem.leftBarButtonItem = self.menuBtnNew;
    } else {
        self.navigationItem.leftBarButtonItem = self.menuBtn;
    }
    
    UIBarButtonItem* rightButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_account_btn"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
    self.navigationItem.rightBarButtonItem = rightButtonItem;
}

- (void)didTapRootTitle
{
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    
    [self showTextHud:[NSString stringWithFormat:@"version: %@", version]];
}


#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuViewDidTapBlankView
{
    [self hideMenu];
}
- (void)rootMenuItemPressedType:(int)type
{
    [self hideMenu];
    switch (type) {
        case 1:
        {
#warning TODO
//            UIViewController* vc = [[QSS02ShandianViewController alloc] init];
//            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        case 2:
        {
            UIViewController *vc = [[QSS08FashionViewController alloc] init];
            [self.navigationController  pushViewController:vc animated:YES];
            break;

        }
        case 3:
        {
#warning TODO remove
//            UIViewController* vc = [[QSP01ModelListViewController alloc] init];
//            [self.navigationController pushViewController:vc animated:YES];
//            break;
        }
        case 4:
        {
#warning TODO remove
//            UIViewController* vc = [[QSS06CompareViewController alloc] init];
//            [self.navigationController pushViewController:vc animated:YES];
//            break;
        }
        case 9:
        {
//            UIViewController* vc = [[QSP03BrandListViewController alloc] init];
//            [self.navigationController pushViewController:vc animated:YES];
            UIViewController *vc = [[QSS17TopShowsViewController alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        case 8:
        {

//    UIViewController* vc = [[QSS02ShandianViewController alloc] init];
//    [self.navigationController pushViewController:vc animated:YES];
    UIViewController *vc = [[QSS02ItemFeedingViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        default:
        {
            UIViewController* vc = [[QSS02CategoryViewController alloc] initWithCategory:type];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
    }
    
    
}

#pragma mark - IBAction
- (void)menuButtonPressed
{
    self.navigationItem.leftBarButtonItem = self.menuBtn;
    [QSUserManager shareUserManager].lastClickMenuDate = [NSDate date];
    
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

- (void)accountButtonPressed
{
    [self hideMenu];
    
    QSUserManager* userManager = [QSUserManager shareUserManager];
    //    if (userManager.fIsLogined && !userManager.userInfo) {
    //        //还未获取到用户数据
    //        [self showTextHud:@"请稍后再试"];
    //        [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
    //    } else
    if (!userManager.userInfo) {
        //未登陆
        UIViewController *vc = [[QSU07RegisterViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    } else {
        //已登陆
        UIViewController* vc = [[QSU01UserDetailViewController alloc] initWithCurrentUser];
        [self.navigationController pushViewController:vc animated:YES];
    }
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
@end
