//
//  QSU14FavoriteViewController.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//  我的收藏

#import "QSU14FavoriteViewController.h"
#import "QSU14DisplayCell.h"
#import "QSNetworkKit.h"
#import "QSShowUtil.h"
#import "Common.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSUserManager.h"

#define PAGE_ID @"U14 - 收藏搭配"

@interface QSU14FavoriteViewController ()


//tableView 数据源
@property (nonatomic, strong)NSMutableArray *dataArray;

@property (nonatomic, strong)QSFavorTableViewProvider *favoProvider;

@end

@implementation QSU14FavoriteViewController

#pragma mark - Init
- (instancetype)init {
    if (self = [super initWithNibName:@"QSU14FavoriteViewController" bundle:nil]) {
        _dataArray = [NSMutableArray array];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"我的收藏";
    
    [self hideNaviBackBtnTitle];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    
    [self configProvider];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [MobClick endLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

#pragma mark -- provider
- (void)configProvider
{
    self.favoProvider = [[QSFavorTableViewProvider alloc] init];
    self.favoProvider.delegate = self;
#warning TODO remove currentVC
    self.favoProvider.currentVC = self;
    [self.favoProvider bindWithTableView:self.tableView];

    
    self.favoProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getLikeFeedingUser:[QSUserManager shareUserManager].userInfo page:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.favoProvider reloadData];

}

#pragma mark - QSU14FavoriteViewController
- (void)didSelectionShow:(NSDictionary*)showDict ofProvider:(QSFavorTableViewProvider*)provider
{
    [self showShowDetailViewController:showDict];
}


- (void)handleError:(NSError *)error
{
    [self showErrorHudWithError:error];
}
@end
