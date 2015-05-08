//
//  QSS02ShandianViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 2/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS02ItemFeedingViewController.h"
#import "QSG01ItemWebViewController.h"
#import "QSNetworkKit.h"
#import "QSItemUtil.h"
#import "QSS11CreateTradeViewController.h"
#import "QSS10ItemDetailVideoViewController.h"
#import "UIViewController+QSExtension.h"

#define PAGE_ID @"S02 - 潮流单品"

@interface QSS02ItemFeedingViewController ()

@property (strong, nonatomic) QSItemCollectionViewProvider* itemProvider;

@end

@implementation QSS02ItemFeedingViewController
#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSS02ShandianViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configProvider];
    [self configView];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [MobClick beginLogPageView:PAGE_ID];
    [self.itemProvider refreshClickedData];
    self.navigationController.navigationBarHidden = NO;
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -
- (void)configView
{
    self.title = @"潮流单品";
    [self hideNaviBackBtnTitle];
    self.navigationController.navigationBarHidden = NO;
    
}
- (void)configProvider
{
    self.itemProvider = [[QSItemCollectionViewProvider alloc] init];
    self.itemProvider.type = QSItemWaterfallDelegateObjTypeWithDate;
    [self.itemProvider bindWithCollectionView:self.collectionView];
    self.itemProvider.delegate = self;
    self.itemProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getItemFeedingRandomPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.itemProvider fetchDataOfPage:1];
}
#pragma mark -
- (void)didClickItem:(NSDictionary*)itemDict
{
#warning TODO change
    UIViewController* vc = [[QSS10ItemDetailVideoViewController alloc] initWithItem:itemDict];
//    UIViewController* vc = [[QSS11CreateTradeViewController alloc] initWithDict:itemDict];
    [self.navigationController pushViewController:vc animated:YES];
}
@end
