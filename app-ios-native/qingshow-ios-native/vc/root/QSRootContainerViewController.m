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
#import "QSNavigationController.h"

@interface QSRootContainerViewController ()

@property (strong, nonatomic) UIViewController* contentVc;


@end

@implementation QSRootContainerViewController



#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.menuView triggerItemTypePressed:QSRootMenuItemMy];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
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
        case QSRootMenuItemMy:{
            QSU01UserDetailViewController* u01Vc = [[QSU01UserDetailViewController alloc] initWithCurrentUser];
            u01Vc.menuProvider = self;
            vc = u01Vc;
            break;
        }
        case QSRootMenuItemMyFavor:{
            break;
        }
        case QSRootMenuItemMeida:{
            break;
        }
        case QSRootMenuItemSetting:
        {
            UIStoryboard *tableViewStoryboard = [UIStoryboard storyboardWithName:@"QSU02UserSetting" bundle:nil];
            QSU02UserSettingViewController *settingVc = [tableViewStoryboard instantiateViewControllerWithIdentifier:@"U02UserSetting"];
            settingVc.menuProvider = self;
            vc = settingVc;
            break;
        }
    }
    [self showVc:vc];
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
    
    if (self.contentVc) {
#warning Transition
        
    }

    [vc didMoveToParentViewController:self];    //Call after transition
    self.contentVc = nav;

    
}

@end
