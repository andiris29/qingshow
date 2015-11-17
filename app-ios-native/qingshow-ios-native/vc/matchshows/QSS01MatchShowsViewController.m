//
//  QSS01MatchShowsViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS01MatchShowsViewController.h"
#import "QSMatchCollectionViewProvider.h"
#import "QSBlock.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSShareViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU01UserDetailViewController.h"
#import "QSPaymentService.h"
#import "UIViewController+QSExtension.h"
#import "QSS11CreateTradeViewController.h"
#import "QSS23SearchViewController.h"
#import "QSUnreadManager.h"

#define PAGE_ID @"新美搭榜单"

@interface QSS01MatchShowsViewController ()<UICollectionViewDelegate,QSMatchCollectionViewProviderDelegate>

@property (nonatomic,assign) NSInteger segIndex;
@property (nonatomic,strong) UISegmentedControl *segmentControl;
@property (nonatomic,strong) QSMatchCollectionViewProvider *matchCollectionViewProvider;
@property (strong, nonatomic) UIBarButtonItem* searchMenuBtn;

- (IBAction)backToTopBtnPressed:(id)sender;

@end

@implementation QSS01MatchShowsViewController

- (UIBarButtonItem*)searchMenuBtn {
    if (!_searchMenuBtn) {
        _searchMenuBtn = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"s01_search_btn"] style:UIBarButtonItemStylePlain target:self action:@selector(searchBtnPressed:)];
    }
    return _searchMenuBtn;
}

- (instancetype)init {
    self = [super initWithNibName:@"QSS01MatchShowsViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib
    _matchCollectionViewProvider = [[QSMatchCollectionViewProvider alloc]init];
    _matchCollectionViewProvider.delegate = self;
    _matchCollectionViewProvider.hasRefreshControl = YES;
    _backToTopbtn.hidden = YES;
    [self configNav];
    [self configProvider];

}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.matchCollectionViewProvider refreshVisibleData];
}
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.matchCollectionViewProvider cancelImageLoading];
}

- (void)configProvider
{
   
    [_matchCollectionViewProvider bindWithCollectionView:self.collectionView];
    _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        return [SHARE_NW_ENGINE getfeedingMatchFeatured:nil page:page onSucceed:succeedBlock onError:errorBlock];
    };
    //NSLog(@" new count == %@",self.matchCollectionViewProvider.resultArray);
    [_matchCollectionViewProvider fetchDataOfPage:1];
    [self.matchCollectionViewProvider reloadData];
}

- (void)configNav
{

    _segmentControl = [[UISegmentedControl alloc]initWithItems:@[@"达人",@"最热",@"最新"]];
    _segmentControl.frame = CGRectMake(0, 0, 180, 25);
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000]} forState:UIControlStateNormal];
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateHighlighted];
    [_segmentControl addTarget:self action:@selector(changeEvents) forControlEvents:UIControlEventValueChanged];
    _segmentControl.tintColor = [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000];
    
    self.navigationItem.titleView = _segmentControl;
    _segmentControl.selectedSegmentIndex = 0;
    [self.navigationController.navigationBar removeGestureRecognizer:self.showVersionTapGesture];

    self.navigationItem.rightBarButtonItem = self.searchMenuBtn;
}


- (void)changeEvents
{
    _segIndex = _segmentControl.selectedSegmentIndex;
    if(_segIndex ==  2)
    {
        _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
            return [SHARE_NW_ENGINE getfeedingMatchNew:nil page:page onSucceed:succeedBlock onError:errorBlock];
        };
        [_matchCollectionViewProvider fetchDataOfPage:1];
        [self reloadCollectionViewData];
        
    }
    else if(_segIndex == 1)
    {
        [self.matchCollectionViewProvider.resultArray removeAllObjects];
        _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
            return [SHARE_NW_ENGINE getfeedingMatchHot:nil page:page onSucceed:succeedBlock onError:errorBlock];
        };
        [_matchCollectionViewProvider fetchDataOfPage:1];
        [self reloadCollectionViewData];
       
    }else if(_segIndex == 0)
    {
        [self.matchCollectionViewProvider.resultArray removeAllObjects];
        _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
            return [SHARE_NW_ENGINE getfeedingMatchFeatured:nil page:page onSucceed:succeedBlock onError:errorBlock];
        };
        [_matchCollectionViewProvider fetchDataOfPage:1];
        [self reloadCollectionViewData];

    }
}
#pragma mark - Delegate
- (void)didSelectedCellInCollectionView:(id)sender
{
    QSS03ShowDetailViewController *vc = [[QSS03ShowDetailViewController alloc] initWithShow:sender];
//    NSLog(@"%@",[QSEntityUtil getStringValue:sender keyPath:@"_id"]) ;
   // vc.menuProvider = self.menuProvider;
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)reloadCollectionViewData
{
    [self hideNewworkWaitingHud];
    MBProgressHUD* hud = [self showNetworkWaitingHud];
    [hud hide:YES];
    [self.matchCollectionViewProvider reloadDataOnCompletion:^{
        [hud hide:YES];
    }];
}

- (void)didClickHeaderImgView:(id)sender
{
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:sender];
    vc.menuProvider = self.menuProvider;
    vc.navigationController.navigationBar.hidden = NO;
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.collectionView.contentOffset.y != 0) {
        _backToTopbtn.hidden = NO;
    }
    else
    {
        _backToTopbtn.hidden = YES;
    }
}
- (IBAction)backToTopBtnPressed:(id)sender {
    CGPoint p = [self.collectionView contentOffset];
    p.y = 0;
    [self.collectionView setContentOffset:p animated:YES];
    _backToTopbtn.hidden = YES;
}

- (void)handleNetworkError:(NSError *)error {
    [self handleError:error];
}

- (void)searchBtnPressed:(id)sender {
    [self.navigationController pushViewController:[[QSS23SearchViewController alloc] init] animated:YES];
}
@end
