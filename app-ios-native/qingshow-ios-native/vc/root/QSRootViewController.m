//
//  QSViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSRootViewController.h"
#import "QSNetworkEngine.h"
#import "QSModelListViewController.h"
#import "QSShowWaterfallDelegateObj.h"

@interface QSRootViewController ()

@property (strong, nonatomic) QSRootMenuView* menuView;
@property (assign, nonatomic) BOOL fIsShowMenu;
@property (strong, nonatomic) QSShowWaterfallDelegateObj* delegateObj;
@end

@implementation QSRootViewController
#pragma mark - 
- (id)init
{
    self = [self initWithNibName:@"QSRootViewController" bundle:nil];
    if (self) {
        
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
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Network
- (void)configDelegateObj
{
    self.delegateObj = [[QSShowWaterfallDelegateObj alloc] init];
    [self.delegateObj bindWithCollectionView:self.collectionView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
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
    UIBarButtonItem* accountItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
    self.navigationItem.rightBarButtonItem = accountItem;
}

#pragma mark - IBAction
- (void)menuButtonPressed
{
    __weak QSRootViewController* weakSelf = self;
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
    NSLog(@"accountBtnPressed");
}

#pragma mark - QSWaterFallCollectionViewCellDelegate
- (void)favorBtnPressed:(QSWaterFallCollectionViewCell*)cell
{
    
    
}
#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuItemPressedType:(int)type
{
    UIViewController* vc = [[QSModelListViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
