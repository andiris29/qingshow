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
#import "QSFavoInfo.h"
#import "Common.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSUserManager.h"


@interface QSU14FavoriteViewController ()


//tableView 数据源
@property (nonatomic, strong)NSMutableArray *dataArray;

@property (nonatomic, strong)QSU14FavoTableViewProvider *favoProvider;

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
    [self.favoProvider registerCell];
    [self setProvider];
    self.title = @"我的收藏";
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    
}

#pragma mark -- provider
- (void)setProvider
{
    self.favoProvider = [[QSU14FavoTableViewProvider alloc] init];
    self.favoProvider.delegate = self;
    [self.favoProvider bindWithTableView:self.tableView];
//    __weak QSU14FavoriteViewController* weakSelf = self;
    self.favoProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getLikeFeedingUser:[QSUserManager shareUserManager].userInfo page:page onSucceed:succeedBlock onError:errorBlock];
//        return [SHARE_NW_ENGINE getTestShowsOnSucceed:succeedBlock onError:errorBlock];
    };
    [self.favoProvider fetchDataOfPage:1];
    [self.favoProvider reloadData];

}

#pragma mark - QSU14FavoriteViewController
- (void)didSelectionShow:(NSDictionary*)showDict ofProvider:(QSU14FavoTableViewProvider*)provider
{
    [self showShowDetailViewController:showDict];
}
- (void)handleError:(NSError *)error
{
    [self showErrorHudWithError:error];
}
@end
