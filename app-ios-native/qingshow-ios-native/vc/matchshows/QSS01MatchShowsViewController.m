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


#define PAGE_ID @"新美搭榜单"

@interface QSS01MatchShowsViewController ()<UICollectionViewDelegate,QSMatchCollectionViewProviderDelegate>

@property (nonatomic,assign) NSInteger segIndex;
@property (nonatomic,strong) UISegmentedControl *segmentControl;
@property (nonatomic,strong) QSMatchCollectionViewProvider *matchCollectionViewProvider;

@property (strong, nonatomic) QSS11NewTradeNotifyViewController* s11NotiVc;


@end

@implementation QSS01MatchShowsViewController

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
    _matchCollectionViewProvider.type = 1;
    _backToTopbtn.hidden = YES;
    [self configNav];
    [self configProvider];
//    [self showTradeNotiViewOfTradeId:@"55c60829e92f835707e83fc7"];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}

- (void)configProvider
{
   
    [_matchCollectionViewProvider bindWithCollectionView:self.collectionView];
    _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        return [SHARE_NW_ENGINE getfeedingMatchHot:nil page:page onSucceed:succeedBlock onError:errorBlock];
    };
    //NSLog(@" new count == %@",self.matchCollectionViewProvider.resultArray);
    [_matchCollectionViewProvider fetchDataOfPage:1];
    [self.matchCollectionViewProvider reloadData];
}

- (void)configNav
{
    
    _segmentControl = [[UISegmentedControl alloc]initWithItems:@[@"最热",@"最新"]];
    _segmentControl.frame = CGRectMake(0, 0, 140, 25);
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000]} forState:UIControlStateNormal];
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateHighlighted];
    [_segmentControl addTarget:self action:@selector(changeEvents) forControlEvents:UIControlEventValueChanged];
    _segmentControl.tintColor = [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000];
    
    self.navigationItem.titleView = _segmentControl;
    _segmentControl.selectedSegmentIndex = 0;
    [self.navigationController.navigationBar removeGestureRecognizer:self.showVersionTapGesture];
}


- (void)changeEvents
{
    _segIndex = _segmentControl.selectedSegmentIndex;
    if(_segIndex ==  1)
    {
        
        _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
            return [SHARE_NW_ENGINE getfeedingMatchNew:nil page:page onSucceed:succeedBlock onError:errorBlock];
        };
        [_matchCollectionViewProvider fetchDataOfPage:1];
        [self reloadCollectionViewData];
        
    }
    else if(_segIndex == 0)
    {
        [self.matchCollectionViewProvider.resultArray removeAllObjects];
        _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
            return [SHARE_NW_ENGINE getfeedingMatchHot:nil page:page onSucceed:succeedBlock onError:errorBlock];
        };
        [_matchCollectionViewProvider fetchDataOfPage:1];
        [self reloadCollectionViewData];
       
    }
}
#pragma mark - Delegate
- (void)didSelectedCellInCollectionView:(id)sender
{

    QSS03ShowDetailViewController *vc = [[QSS03ShowDetailViewController alloc]initWithShowId:[QSEntityUtil getStringValue:sender keyPath:@"_id"]];
//    NSLog(@"%@",[QSEntityUtil getStringValue:sender keyPath:@"_id"]) ;
   // vc.menuProvider = self.menuProvider;
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)reloadCollectionViewData
{
    MBProgressHUD* hud = [self showNetworkWaitingHud];
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
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/
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

- (void)showTradeNotiViewOfTradeId:(NSString*)tradeId {
    [SHARE_NW_ENGINE queryTradeDetail:tradeId onSucceed:^(NSDictionary *dict) {
        self.s11NotiVc = [[QSS11NewTradeNotifyViewController alloc] initWithDict:dict];
        self.s11NotiVc.delelgate = self;
        self.s11NotiVc.view.frame = self.navigationController.view.bounds;
        [self.navigationController.view addSubview:self.s11NotiVc.view];
    } onError:^(NSError *error) {
        
    }];
}
- (void)didClickClose:(QSS11NewTradeNotifyViewController *)vc
{
    [self.s11NotiVc.view removeFromSuperview];
    self.s11NotiVc = nil;
}

- (void)didClickPay:(QSS11NewTradeNotifyViewController*)vc {
    
    [SHARE_PAYMENT_SERVICE sharedForTrade:vc.tradeDict onSucceed:^{
        [self didClickClose:vc];
        QSS11CreateTradeViewController* v = [[QSS11CreateTradeViewController alloc] initWithDict:vc.tradeDict];
        v.menuProvider = self.menuProvider;
        [self.navigationController pushViewController:v animated:YES];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
@end
