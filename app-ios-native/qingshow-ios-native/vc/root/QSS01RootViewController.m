//
//  QSViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import "UIViewController+ShowHud.h"

#import <QuartzCore/QuartzCore.h>

#import "QSS01RootViewController.h"
#import "QSNetworkKit.h"
#import "QSP01ModelListViewController.h"
#import "QSP02ModelDetailViewController.h"
#import "QSP03BrandListViewController.h"
#import "QSS08PreviewViewController.h"

#import "QSS02CategoryViewController.h"

#import "QSU02UserSettingViewController.h"
#import "QSS02ShandianViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "QSS06CompareViewController.h"

#import "QSUserManager.h"
#import "QSU01UserDetailViewController.h"
#import "QSShowUtil.h"
#import "QSError.h"
#import "UIViewController+QSExtension.h"
#import "UIImage+BlurryImage.h"
#import "QSAppDelegate.h"

#define PAGE_ID @"S01 - 倾秀首页"


@interface QSS01RootViewController ()

@property (strong, nonatomic) QSRootMenuView* menuView;
@property (assign, nonatomic) BOOL fIsShowMenu;
@property (strong, nonatomic) QSShowCollectionViewProvider* delegateObj;
//@property (assign, nonatomic) BOOL fISLogined;

@property (assign, nonatomic) BOOL fIsFirstLoad;

@end

@implementation QSS01RootViewController
#pragma mark - 
- (id)init
{
    self = [self initWithNibName:@"QSS01RootViewController" bundle:nil];
    if (self) {
        [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view, typically from a nib.MyLayout *layout=[[MyLayout alloc]init];
    [self configDelegateObj];
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
    
    [self.delegateObj refreshClickedData];
    
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideMenu];
    
    if (self.fIsFirstLoad) {
        self.fIsFirstLoad = NO;
    }
    
    [MobClick endLogPageView:PAGE_ID];
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Network
- (void)configDelegateObj
{
    self.delegateObj = [[QSShowCollectionViewProvider alloc] init];
    self.delegateObj.delegate = self;
    self.delegateObj.type = QSShowWaterfallDelegateObjTypeWithDate;
    [self.delegateObj bindWithCollectionView:self.collectionView];
    __weak QSS01RootViewController* weakSelf = self;
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingType:0 page:page onSucceed:succeedBlock onError:^(NSError *error) {
            if ([error.domain isEqualToString:NSURLErrorDomain] && error.code == -1009) {
                UIAlertView* a = [[UIAlertView alloc] initWithTitle:@"未连接网络或信号不好" message:nil delegate:weakSelf cancelButtonTitle:@"确定" otherButtonTitles: nil];
                [a show];
            } else {
                errorBlock(error);
            }

        }];
    };
    [self.delegateObj fetchDataOfPage:1];
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
    
    UIBarButtonItem* menuItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
    self.navigationItem.leftBarButtonItem = menuItem;
    
    UIBarButtonItem* rightButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_account_btn"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
    self.navigationItem.rightBarButtonItem = rightButtonItem;
}

- (void)didTapRootTitle
{
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    
    [self showTextHud:[NSString stringWithFormat:@"version: %@", version]];
}

#pragma mark - IBAction
- (void)menuButtonPressed
{
    __weak QSS01RootViewController* weakSelf = self;
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

// TODO Remove code block
#pragma mark - QSWaterFallCollectionViewCellDelegate
- (void)addFavorShow:(NSDictionary*)showDict
{
    if ([QSShowUtil getIsLike:showDict]) {
        [SHARE_NW_ENGINE unlikeShow:showDict onSucceed:^{
            [self showSuccessHudWithText:@"取消喜欢成功"];
            [self.delegateObj updateShow:showDict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    } else {
        [SHARE_NW_ENGINE likeShow:showDict onSucceed:^{
            [self showSuccessHudWithText:@"喜欢成功"];
            [self.delegateObj updateShow:showDict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    }
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
            UIViewController* vc = [[QSS02ShandianViewController alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        case 3:
        {
            UIViewController* vc = [[QSP01ModelListViewController alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        case 4:
        {
            UIViewController* vc = [[QSS06CompareViewController alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        case 9:
        {
            UIViewController* vc = [[QSP03BrandListViewController alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        case 8:
        {
            UIViewController* vc = [[QSS08PreviewViewController alloc] init];
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

#pragma mark - QSShowWaterfallDelegateObjDelegate
- (void)hideMenu
{
    if (self.fIsShowMenu)
    {
        __weak QSS01RootViewController* weakSelf = self;
        [self.menuView hideMenuAnimationComple:^{
            weakSelf.menuView.hidden = YES;
        }];
        self.fIsShowMenu = NO;
    }
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{

}
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [self hideMenu];
}

#pragma mark -
- (void)didClickShow:(NSDictionary*)showDict
{
    [self hideMenu];
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:NO];

    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromRight;
    
    [self.navigationController.view.layer addAnimation:tran forKey:@"transition_to_show_detail"];
}

- (void)didClickPeople:(NSDictionary *)peopleDict
{
    [self hideMenu];
    UIViewController* vc = [self generateDetailViewControlOfPeople:peopleDict];
    [self.navigationController pushViewController:vc animated:NO];
    
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromRight;
    [self.navigationController.view.layer addAnimation:tran forKey:@"transition_to_people_detail"];
}

- (void)handleNetworkError:(NSError*)error
{
    [self handleError:error];
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self hideMenu];
}
#pragma mark - AlertView
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    [self.delegateObj reloadData];
}
@end
