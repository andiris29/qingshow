//
//  QSS01MatchShowsViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS01MatchShowsViewController.h"
#import "QSNewestHourViewController.h"
#import "QSMatchCollectionViewProvider.h"
#import "QSMatcherTableViewProvider.h"
#import "QSError.h"
#import "QSBlock.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSShareViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU01UserDetailViewController.h"
#import "QSPaymentService.h"
#import "UIViewController+QSExtension.h"
#import "QSS11CreateTradeViewController.h"
#import "QSUnreadManager.h"
#import "CKCalendarView.h"
#import "QSDateUtil.h"

#define PAGE_ID @"新美搭榜单"


@interface QSS01MatchShowsViewController ()<QSMatchCollectionViewProviderDelegate, QSMatcherTableViewProviderDelegate, CKCalendarDelegate>

@property (weak, nonatomic) IBOutlet UIButton *calendarBtn;


@property (nonatomic, strong) UISegmentedControl *segmentControl;
@property (strong, nonatomic) NSArray* viewsArray;
#pragma mark Provider
@property (nonatomic, strong) QSMatchCollectionViewProvider *darenProvider;
@property (nonatomic, strong) QSMatchCollectionViewProvider *hotProvider;
@property (nonatomic, strong) QSMatcherTableViewProvider* newestProvider;

#pragma mark S11
//@property (strong, nonatomic) QSS12NewTradeNotifyViewController* s11NotiVc;
@property (strong, nonatomic) QSS12NewTradeExpectableViewController* s11NotiVc;
@property (strong, nonatomic) NSDate* currentDate;

- (IBAction)backToTopBtnPressed:(id)sender;


@property (strong, nonatomic) CKCalendarView* calendarView;
@end

@implementation QSS01MatchShowsViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSS01MatchShowsViewController" bundle:nil];
    if (self) {
        self.currentDate = [NSDate date];
    }
    return self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self _configNav];
    [self _configProvider];
    self.viewsArray = @[self.darenCollectionView, self.hotCollectionView, self.newestTableView];
    

    self.calendarContainerView.frame = self.navigationController.view.bounds;
    self.calendarView = [[CKCalendarView alloc] initWithStartDay:startSunday frame:self.calendarContainerView.bounds];
    [self.calendarContainerView addSubview:self.calendarView];
    self.calendarView.delegate = self;
    self.calendarView.selectedDate = self.currentDate;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.darenProvider refreshVisibleData];
    [self.hotProvider refreshVisibleData];
    [self.newestProvider refreshVisibleData];
    
    //Hide Navigation Shadow
    [self.navigationController.navigationBar setBackgroundImage:[[UIImage alloc] init] forBarPosition:UIBarPositionAny barMetrics:UIBarMetricsDefault];
    [self.navigationController.navigationBar setShadowImage:[[UIImage alloc] init]];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.darenProvider cancelImageLoading];
    [self.hotProvider cancelImageLoading];
    [self.newestProvider cancelImageLoading];

}
- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    //Show Navigation Shadow
    [self.navigationController.navigationBar setBackgroundImage:nil forBarPosition:UIBarPositionAny barMetrics:UIBarMetricsDefault];
    [self.navigationController.navigationBar setShadowImage:nil];
}
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.calendarView.center = self.calendarContainerView.center;
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - QSMatchCollectionViewProviderDelegate
- (void)matcherCollectionViewProvider:(QSMatchCollectionViewProvider*)provider
                         didClickShow:(NSDictionary*)showDict
{
    QSS03ShowDetailViewController *vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)matcherCollectionViewProvider:(QSMatchCollectionViewProvider*)provider
                       didClickPeople:(NSDictionary*)peopleDict
{
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDict];
    vc.menuProvider = self.menuProvider;
    vc.navigationController.navigationBar.hidden = NO;
    [self.navigationController pushViewController:vc animated:YES];
    
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    UIScrollView* sv = self.viewsArray[self.segmentControl.selectedSegmentIndex];
    if (sv.contentOffset.y != 0) {
        _backToTopbtn.hidden = NO;
    } else {
        _backToTopbtn.hidden = YES;
    }
}

#pragma mark - IBAction
- (void)_segmentChanged
{
    NSInteger segIndex = self.segmentControl.selectedSegmentIndex;
    for (NSInteger i = 0; i < self.viewsArray.count; i++) {
        UIScrollView* sv = self.viewsArray[i];
        sv.hidden = segIndex != i;
    }
}

- (IBAction)backToTopBtnPressed:(id)sender {
    UIScrollView* currentScrollView = self.viewsArray[self.segmentControl.selectedSegmentIndex];
    CGPoint p = [currentScrollView contentOffset];
    p.y = 0;
    [currentScrollView setContentOffset:p animated:YES];
    _backToTopbtn.hidden = YES;
}
- (IBAction)calendarBtnPressed:(id)sender {
    self.calendarContainerView.hidden = NO;
}

#pragma mark - s11
- (void)showTradeNotiViewOfTradeId:(NSString*)tradeId{
    [SHARE_NW_ENGINE queryTradeDetail:tradeId onSucceed:^(NSDictionary *dict) {
        [[QSUnreadManager getInstance] clearTradeUnreadId:tradeId];
        self.s11NotiVc = [[QSS12NewTradeExpectableViewController alloc] initWithDict:dict];
        self.s11NotiVc.delelgate = self;
        self.s11NotiVc.view.frame = self.navigationController.view.bounds;
        [self.navigationController.view addSubview:self.s11NotiVc.view];
    } onError:^(NSError *error) {
        
    }];
}
- (void)didClickClose:(QSS12NewTradeExpectableViewController *)vc
{
    [self.s11NotiVc.view removeFromSuperview];
    self.s11NotiVc = nil;
}

- (void)didClickPay:(QSS12NewTradeExpectableViewController*)vc {
    NSDictionary* tradeDict = vc.tradeDict;
    NSNumber* actualPrice = vc.expectablePrice;
    NSDictionary* paramDict = nil;
    if (actualPrice) {
        paramDict = @{@"actualPrice" : vc.expectablePrice};
    }
    [SHARE_PAYMENT_SERVICE sharedForTrade:tradeDict onSucceed:^(NSDictionary* d){
        [self didClickClose:vc];
        QSS11CreateTradeViewController* v = [[QSS11CreateTradeViewController alloc] initWithDict:d];
        v.menuProvider = self.menuProvider;
        [self.navigationController pushViewController:v animated:YES];
    } onError:^(NSError *error) {
        [vc handleError:error];
    }];
    
}

#pragma mark - Private
- (void)_configProvider
{
    //Da ren
    self.darenProvider = [[QSMatchCollectionViewProvider alloc] init];
    self.darenProvider.currentDate = self.currentDate;
    self.darenProvider.delegate = self;
    self.darenProvider.hasRefreshControl = YES;
    _backToTopbtn.hidden = YES;
    
    [self.darenProvider bindWithCollectionView:self.darenCollectionView];
    __weak QSS01MatchShowsViewController* weakSelf = self;
    self.darenProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        NSDate* currentDate = [QSDateUtil clearTimeFromDate:weakSelf.currentDate];
        NSDate* nextDate = [currentDate dateByAddingTimeInterval:60 * 60 * 24];
        return [SHARE_NW_ENGINE getfeedingMatchFeaturedFromDate:currentDate
                                                         toDate:nextDate
                                                           page:page
                                                      onSucceed:succeedBlock
                                                        onError:errorBlock];
    };
    self.darenProvider.headerNetworkBlock = ^MKNetworkOperation* (TopOwnerBlock succeedBlock, ErrorBlock errorBlock) {
        
        return [SHARE_NW_ENGINE aggregationFeaturedTopOwners:[QSDateUtil clearTimeFromDate:weakSelf.currentDate]
                                                   onSucceed:succeedBlock
                                                     onError:errorBlock];
    };
    
    [self.darenProvider reloadData];
    
    //hot
    self.hotProvider = [[QSMatchCollectionViewProvider alloc] init];
    self.hotProvider.currentDate = self.currentDate;
    self.hotProvider.delegate = self;
    self.hotProvider.hasRefreshControl = YES;

    _backToTopbtn.hidden = YES;
    
    [self.hotProvider bindWithCollectionView:self.hotCollectionView];
    self.hotProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        NSDate* currentDate = [QSDateUtil clearTimeFromDate:weakSelf.currentDate];
        NSDate* nextDate = [currentDate dateByAddingTimeInterval:60 * 60 * 24];
        return [SHARE_NW_ENGINE getfeedingMatchHotFromDate:currentDate
                                                    toDate:nextDate
                                                      page:page
                                                 onSucceed:succeedBlock
                                                   onError:errorBlock];
    };
    self.hotProvider.headerNetworkBlock = ^MKNetworkOperation* (TopOwnerBlock succeedBlock, ErrorBlock errorBlock) {
        
        return [SHARE_NW_ENGINE aggregationMatchHotTopOwners:[QSDateUtil clearTimeFromDate:weakSelf.currentDate]
                                                   onSucceed:succeedBlock
                                                     onError:errorBlock];
    };
    
    [self.hotProvider reloadData];
    
    //newest
    self.newestProvider = [[QSMatcherTableViewProvider alloc] init];
    self.newestProvider.delegate = self;
    self.newestProvider.hasPaging = NO;
    [self.newestProvider bindWithTableView:self.newestTableView];
    self.newestProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        
        return [SHARE_NW_ENGINE aggregationMatchNew:[QSDateUtil clearTimeFromDate:weakSelf.currentDate]
                                          onSucceed:succeedBlock
                                            onError:errorBlock];
    };
    [self.newestProvider reloadData];
}

- (void)_configNav
{
    _segmentControl = [[UISegmentedControl alloc]initWithItems:@[@"达人",@"最热",@"最新"]];
    _segmentControl.frame = CGRectMake(0, 0, 180, 25);
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000]} forState:UIControlStateNormal];
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateHighlighted];
    [_segmentControl addTarget:self action:@selector(_segmentChanged) forControlEvents:UIControlEventValueChanged];
    _segmentControl.tintColor = [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000];
    
    _segmentControl.selectedSegmentIndex = 0;
    [self.segmentContainerView addSubview:self.segmentControl];
    self.segmentControl.center = CGPointMake(self.segmentContainerView.bounds.size.width / 2, self.segmentContainerView.bounds.size.height / 2);
    self.segmentContainerView.backgroundColor = self.navigationController.navigationBar.barTintColor;
    self.title = @"美搭榜单";
}

- (void)_reloadCollectionViewData
{
    [self hideNewworkWaitingHud];
    MBProgressHUD* hud = [self showNetworkWaitingHud];
    [hud hide:YES];
    [self.darenProvider reloadDataOnCompletion:^{
        [hud hide:YES];
    }];
}

#pragma mark - CKCalendarDelegate
- (void)calendar:(CKCalendarView *)calendar didSelectDate:(NSDate *)date {
    self.calendarContainerView.hidden = YES;
    self.calendarView.selectedDate = date;
    self.currentDate = date;
    self.darenProvider.currentDate = self.currentDate;
    [self.darenProvider reloadData];
    [self.newestProvider reloadData];
    self.hotProvider.currentDate = self.currentDate;
    [self.hotProvider reloadData];
}
#pragma mark - QSMatcherTableViewProvider
- (void)provider:(QSMatcherTableViewProvider*)provider didClickDate:(NSDate*)date {
    QSNewestHourViewController* vc = [[QSNewestHourViewController alloc] initWithDate:date];
    [self.navigationController pushViewController:vc animated:YES];
}
@end
