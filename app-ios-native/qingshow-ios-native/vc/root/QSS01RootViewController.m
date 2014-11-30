//
//  QSViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import "UIViewController+ShowHud.h"

#import "QSS01RootViewController.h"
#import "QSNetworkEngine.h"
#import "QSP01ModelListViewController.h"
#import "QSP02ModelDetailViewController.h"
#import "QSP03BrandListViewController.h"

#import "QSS02CategoryViewController.h"

#import "QSU02UserSettingViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU06LoginViewController.h"
#import "QSS06CompareViewController.h"

#import "QSUserManager.h"
#import "QSU01UserDetailViewController.h"
#import "QSShowUtil.h"

@interface QSS01RootViewController ()

@property (strong, nonatomic) QSRootMenuView* menuView;
@property (assign, nonatomic) BOOL fIsShowMenu;
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* delegateObj;
//@property (assign, nonatomic) BOOL fISLogined;
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
    [self.menuContainer addSubview:menuView];
    self.menuView = menuView;
    self.fIsShowMenu = NO;
    menuView.delegate = self;
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    }
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideMenu];
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Network
- (void)configDelegateObj
{
    self.delegateObj = [[QSShowCollectionViewDelegateObj alloc] init];
    self.delegateObj.delegate = self;
    [self.delegateObj bindWithCollectionView:self.collectionView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.delegateObj.type = QSShowWaterfallDelegateObjTypeWithDate;
    [self.delegateObj fetchDataOfPage:1];
}

#pragma mark - Configure View
- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    UIBarButtonItem* menuItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
    self.navigationItem.leftBarButtonItem = menuItem;
    
    UIBarButtonItem* rightButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
    self.navigationItem.rightBarButtonItem = rightButtonItem;
}

#pragma mark - IBAction
- (void)menuButtonPressed
{
    __weak QSS01RootViewController* weakSelf = self;
    if (self.fIsShowMenu)
    {
        [self.menuView hideMenuAnimationComple:^{
            weakSelf.menuContainer.hidden = YES;
        }];
    }
    else
    {
        weakSelf.menuContainer.hidden = NO;
        [self.menuView showMenuAnimationComple:^{
        }];
    }
    self.fIsShowMenu = !self.fIsShowMenu;
}

- (void)accountButtonPressed
{
    [self hideMenu];
    
    QSUserManager* userManager = [QSUserManager shareUserManager];
    if (userManager.fIsLogined && !userManager.userInfo) {
        //还未获取到用户数据
        [self showTextHud:@"请稍后再试"];
        [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
    } else if (!userManager.fIsLogined) {
        //未登陆
        UIViewController *vc = [[QSU06LoginViewController alloc]initWithNibName:@"QSU06LoginViewController" bundle:nil];
        [self.navigationController pushViewController:vc animated:YES];
    } else {
        //已登陆
        UIViewController* vc = [[QSU01UserDetailViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
        

    }
}

#pragma mark - QSWaterFallCollectionViewCellDelegate
- (void)addFavorShow:(NSDictionary*)showDict
{
#warning disable becuase server bug
    return;
    if ([QSShowUtil getIsLike:showDict]) {
        [SHARE_NW_ENGINE unlikeShow:showDict onSucceed:^{
            [self.delegateObj reloadData];
        } onError:^(NSError *error) {
            [self showErrorHudWithError:error];
        }];
    } else {
        [SHARE_NW_ENGINE likeShow:showDict onSucceed:^{
            [self.delegateObj reloadData];
        } onError:^(NSError *error) {
            
            [self showErrorHudWithError:error];
        }];
    }
}
#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuItemPressedType:(int)type
{
    [self hideMenu];
    switch (type) {
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
            return;
            UIViewController* vc = [[QSP03BrandListViewController alloc] init];
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
            weakSelf.menuContainer.hidden = YES;
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
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPeople:(NSDictionary *)peopleDict
{
    [self hideMenu];
    UIViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)handleNetworkError:(NSError*)error
{
    [self showErrorHudWithError:error];
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self hideMenu];
}
@end
