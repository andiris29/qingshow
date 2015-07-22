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
#import "QSU07RegisterViewController.h"
#import "QSUserManager.h"
#import "QSS20MatcherViewController.h"
#import "QSS01MatchShowsViewController.h"
#import "QSPnsNotificationName.h"
#import "QSCommonUtil.h"
#import "QSS03ShowDetailViewController.h"
#import "QSS04CommentListViewController.h"

@interface QSRootContainerViewController ()

@property (strong, nonatomic) UINavigationController* contentNavVc;
@property (strong, nonatomic) UIViewController* contentVc;

@end

@implementation QSRootContainerViewController

- (instancetype)init {
    self = [super init];
    if (self){
        [self registerForPnsNotifications];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];

}

- (void)showDefaultVc {

    [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];

}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    
    if (![QSUserManager shareUserManager].userInfo) {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    }
}
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.contentNavVc.view.frame = self.view.bounds;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}


#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuViewDidTapBlankView
{
    [self hideMenu];
}
- (void)rootMenuItemPressedType:(QSRootMenuItemType)type oldType:(QSRootMenuItemType)oldType
{
    [super rootMenuItemPressedType:type oldType:oldType];
    [self hideMenu];
    if (oldType != type) {
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
    } else {
        if ([self.contentNavVc isKindOfClass:[UINavigationController class]]) {
            [((UINavigationController*)self.contentNavVc) popToRootViewControllerAnimated:NO];
        }
    }
    
    
}

- (void)showVc:(UIViewController*)vc{
    if (!vc) {
        return;
    }
    
    UINavigationController* nav = [[QSNavigationController alloc] initWithRootViewController:vc];
    nav.navigationBar.translucent = NO;
    self.contentVc = vc;
    vc = nav;
    [self.contentNavVc.view removeFromSuperview];
    [self.contentNavVc willMoveToParentViewController:nil];
    [self.contentNavVc removeFromParentViewController];

    [self.view addSubview:vc.view];
    [self addChildViewController:vc];
    

    [vc didMoveToParentViewController:self];    //Call after transition
    self.contentNavVc = nav;
}
- (void)showRegisterVc {
    if (![QSUserManager shareUserManager].userInfo) {
        [self.navigationController pushViewController:[[QSU07RegisterViewController alloc] init] animated:YES];
    }
}

#pragma mark - Pns
- (void)registerForPnsNotifications {
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    
    [center addObserver:self selector:@selector(pnsDidReceiveNewShowComment:) name:kPnsNewShowCommentsNotification object:nil];
    [center addObserver:self selector:@selector(pnsDidNewRecommandation:) name:kPnsNewRecommandationNotification object:nil];
    [center addObserver:self selector:@selector(pnsQuestSharingProgress:) name:kPnsQuestSharingProgressNotification object:nil];
    [center addObserver:self selector:@selector(pnsQuestSharingCompleted:) name:kPnsQuestSharingCompleteNotification object:nil];
}
- (void)pnsDidReceiveNewShowComment:(NSNotification*)noti {

    if (self.contentNavVc) {
        NSDictionary* userInfo = noti.userInfo;
        NSString* showId = [QSCommonUtil getStringValue:userInfo key:@"showId"];
        [self.contentNavVc popToRootViewControllerAnimated:NO];
        [self.contentNavVc pushViewController:[[QSS03ShowDetailViewController alloc] initWithShowId:showId] animated:NO];
        [self.contentNavVc pushViewController:[[QSS04CommentListViewController alloc] initWithShowId:showId] animated:NO];
    }
}

- (void)pnsDidNewRecommandation:(NSNotification*)noti {
    if ([QSUserManager shareUserManager].userInfo) {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMy];
        if ([self.contentVc isKindOfClass:[QSU01UserDetailViewController class]]) {
            QSU01UserDetailViewController* myVc = (QSU01UserDetailViewController*)self.contentVc;
            [myVc.badgeView.btnGroup addDotWithType:QSBadgeButtonTypeRecommend];
        }
    }
}

- (void)pnsQuestSharingProgress:(NSNotification*)noti {
    
}

- (void)pnsQuestSharingCompleted:(NSNotification*)noti {
    
}

@end
