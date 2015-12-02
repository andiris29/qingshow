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
#import "QSU14CreateTradeViewController.h"
#import "QSUnreadManager.h"
#import "CKCalendarView.h"
#import "QSDateUtil.h"

#define PAGE_ID @"新美搭榜单"


@interface QSS01MatchShowsViewController ()<QSMatchCollectionViewProviderDelegate, QSMatcherTableViewProviderDelegate, CKCalendarDelegate>

@property (weak, nonatomic) IBOutlet UIButton *calendarBtn;



@property (strong, nonatomic) NSArray* viewsArray;
#pragma mark Provider
@property (nonatomic, strong) NSArray* providerArray;
@property (nonatomic, strong) QSMatchCollectionViewProvider *darenProvider;
@property (nonatomic, strong) QSMatchCollectionViewProvider *hotProvider;
@property (nonatomic, strong) QSMatcherTableViewProvider* newestProvider;

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
    self.calendarContainerView.userInteractionEnabled = YES;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapCalendarContainer:)];
    [self.calendarContainerView addGestureRecognizer:ges];
    self.calendarView.delegate = self;
    self.calendarView.selectedDate = self.currentDate;
    
    
    if (self.defaultSegment) {
        self.segmentControl.selectedSegmentIndex = self.defaultSegment.integerValue;
        self.defaultSegment = nil;
        [self segmentChanged];
    }
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
    CGRect bound = self.segmentControl.bounds;
    bound.size.width = self.segmentContainerView.bounds.size.width - 20;
    self.segmentControl.bounds = bound;
    self.segmentControl.center = CGPointMake(self.segmentContainerView.bounds.size.width / 2, self.segmentContainerView.bounds.size.height / 2);

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
        self.calendarBtn.hidden = YES;
    } else {
        _backToTopbtn.hidden = YES;
        self.calendarBtn.hidden = NO;
    }
}

#pragma mark - IBAction
- (void)segmentChanged
{
    NSInteger segIndex = self.segmentControl.selectedSegmentIndex;
    for (NSInteger i = 0; i < self.viewsArray.count; i++) {
        UIScrollView* sv = self.viewsArray[i];
        sv.hidden = segIndex != i;
    }
    [self _reloadCurrentProvider];
}

- (IBAction)backToTopBtnPressed:(id)sender {
    UIScrollView* currentScrollView = self.viewsArray[self.segmentControl.selectedSegmentIndex];
    CGPoint p = [currentScrollView contentOffset];
    p.y = 0;
    [currentScrollView setContentOffset:p animated:YES];
    _backToTopbtn.hidden = YES;
}
- (IBAction)calendarBtnPressed:(id)sender {
    [self _showCalendar];
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
    
    self.providerArray = @[self.darenProvider,
                           self.hotProvider,
                           self.newestProvider];
    [self _reloadCurrentProvider];
}

- (void)_configNav
{
    _segmentControl = [[UISegmentedControl alloc]initWithItems:@[@"达人",@"最热",@"最新"]];
    _segmentControl.frame = CGRectMake(0, 0, 180, 25);
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000]} forState:UIControlStateNormal];
    [_segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateHighlighted];
    [_segmentControl addTarget:self action:@selector(segmentChanged) forControlEvents:UIControlEventValueChanged];
    _segmentControl.tintColor = [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000];
    
    _segmentControl.selectedSegmentIndex = 0;
    [self.segmentContainerView addSubview:self.segmentControl];
    self.segmentControl.center = CGPointMake(self.segmentContainerView.bounds.size.width / 2, self.segmentContainerView.bounds.size.height / 2);
    self.segmentContainerView.backgroundColor = self.navigationController.navigationBar.barTintColor;
    self.title = @"美搭榜单";
}

- (void)_reloadCurrentProvider {
    NSInteger selectIndex = self.segmentControl.selectedSegmentIndex;
    if (selectIndex >= 0 && selectIndex < self.providerArray.count ) {
        QSAbstractListViewProvider* provider = self.providerArray[self.segmentControl.selectedSegmentIndex];
        [provider reloadData];
    }
}

#pragma mark - CKCalendarDelegate
- (void)calendar:(CKCalendarView *)calendar didSelectDate:(NSDate *)date {
    self.calendarContainerView.hidden = YES;
    self.calendarView.selectedDate = date;
    self.currentDate = date;
    self.darenProvider.currentDate = self.currentDate;
    self.hotProvider.currentDate = self.currentDate;
    [self _reloadCurrentProvider];
}

- (void)didTapCalendarContainer:(UITapGestureRecognizer*)ges {
    [self _hideCalendar];
}

- (void)_showCalendar {
    self.calendarContainerView.hidden = NO;
    self.calendarContainerView.alpha = 0.f;
    [UIView animateWithDuration:0.3 animations:^{
        self.calendarContainerView.alpha = 1.f;
    }];
}
- (void)_hideCalendar {
    [UIView animateWithDuration:0.3 animations:^{
        self.calendarContainerView.alpha = 0.f;
    } completion:^(BOOL finished) {
        self.calendarContainerView.hidden = YES;
    }];
}
#pragma mark - QSMatcherTableViewProvider
- (void)provider:(QSMatcherTableViewProvider*)provider didClickDate:(NSDate*)date {
    QSNewestHourViewController* vc = [[QSNewestHourViewController alloc] initWithDate:date];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)provider:(QSMatcherTableViewProvider*)provider didClickPeople:(NSDictionary*)peopleDict {
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDict];
    vc.menuProvider = self.menuProvider;
    vc.navigationController.navigationBar.hidden = NO;
    [self.navigationController pushViewController:vc animated:YES];
}
@end
