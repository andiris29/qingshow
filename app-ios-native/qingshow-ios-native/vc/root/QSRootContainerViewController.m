//
//  QSRootContainerViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRootContainerViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "QSU01UserDetailViewController.h"
#import "QSU02UserSettingViewController.h"
#import "QSS17ViewController.h"
#import "QSU14FavoriteViewController.h"
#import "QSNavigationController.h"
#import "QSU07RegisterViewController.h"
#import "QSUserManager.h"
#import "QSS20MatcherViewController.h"
#import "QSS01MatchShowsViewController.h"

@interface QSRootContainerViewController ()

@property (strong, nonatomic) UIViewController* contentVc;

@end

@implementation QSRootContainerViewController



#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

- (void)showDefaultVc {
    if ([QSUserManager shareUserManager].userInfo) {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMy];
    } else {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    }

}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    
    if (![QSUserManager shareUserManager].userInfo) {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuViewDidTapBlankView
{
    [self hideMenu];
}
- (void)rootMenuItemPressedType:(QSRootMenuItemType)type
{
    [super rootMenuItemPressedType:type];
    [self hideMenu];
    UIViewController* vc = nil;
    switch (type) {
        case QSRootMenuItemMy: {
            QSU01UserDetailViewController* u01Vc = [[QSU01UserDetailViewController alloc] initWithCurrentUser];
            u01Vc.menuProvider = self;
            vc = u01Vc;
            break;
        }
        case QSRootMenuItemMeida: {
            QSS01MatchShowsViewController * matcherShowVc = [[QSS01MatchShowsViewController alloc] init];
            matcherShowVc.menuProvider = self;
            vc = matcherShowVc;
            break;
        }
        case QSRootMenuItemSetting: {
            QSU02UserSettingViewController *settingVc = [[QSU02UserSettingViewController alloc]init];
            settingVc.menuProvider = self;
            vc = settingVc;
            break;
        }
        case QSRootMenuItemMatcher: {
            QSS20MatcherViewController* matcherVc = [[QSS20MatcherViewController alloc] init];
            matcherVc.menuProvider = self;
            vc = matcherVc;
            break;
        }
    }
    [self showVc:vc];
    
    if (![vc isKindOfClass:[QSS01MatchShowsViewController class]]) {
        [self showRegisterVc];
    }
}

- (void)showVc:(UIViewController*)vc{
    if (!vc) {
        return;
    }
    
    UINavigationController* nav = [[QSNavigationController alloc] initWithRootViewController:vc];
    nav.navigationBar.translucent = NO;
    vc = nav;
    [self.contentVc.view removeFromSuperview];
    [self.contentVc willMoveToParentViewController:nil];
    [self.contentVc removeFromParentViewController];

    [self.view addSubview:vc.view];
    [self addChildViewController:vc];
    

    [vc didMoveToParentViewController:self];    //Call after transition
    self.contentVc = nav;
}
- (void)showRegisterVc {
    if (![QSUserManager shareUserManager].userInfo) {
        [self.navigationController pushViewController:[[QSU07RegisterViewController alloc] init] animated:YES];
    }
}

@end
