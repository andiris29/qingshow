//
//  QSS01MatchShowsViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS01MatchShowsViewController.h"
#import "QSNewestHourViewController.h"
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


@interface QSS01MatchShowsViewController ()<QSMatcherTableViewProviderDelegate, CKCalendarDelegate>

@property (weak, nonatomic) IBOutlet UIButton *calendarBtn;

@property (nonatomic, strong) QSMatcherTableViewProvider* newestProvider;
@property (strong, nonatomic) CKCalendarView* calendarView;

- (IBAction)backToTopBtnPressed:(id)sender;

@property (assign, nonatomic) BOOL fShouldTrackDraging;
@property (assign, nonatomic) CGPoint initOffset;
@end

@implementation QSS01MatchShowsViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSS01MatchShowsViewController" bundle:nil];
    if (self) {
        self.fShouldTrackDraging = NO;
        self.initOffset = CGPointZero;
    }
    return self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self _configNav];
    [self _configProvider];
    

    self.calendarContainerView.frame = self.navigationController.view.bounds;
    self.calendarView = [[CKCalendarView alloc] initWithStartDay:startSunday frame:self.calendarContainerView.bounds];
    [self.calendarContainerView addSubview:self.calendarView];
    self.calendarContainerView.userInteractionEnabled = YES;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapCalendarContainer:)];
    [self.calendarContainerView addGestureRecognizer:ges];
    self.calendarView.delegate = self;
    self.calendarView.selectedDate = [NSDate date];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.newestProvider refreshClickedData];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveUserLoginNoti:) name:kUserLoginNotification object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.newestProvider cancelImageLoading];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    CGPoint center = self.calendarContainerView.center;
    center.y = self.calendarView.bounds.size.height / 2;
    self.calendarView.center = center;
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)backToTopBtnPressed:(id)sender {
    UIScrollView* currentScrollView = self.newestTableView;
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
    _backToTopbtn.hidden = YES;
    
    self.newestProvider = [[QSMatcherTableViewProvider alloc] init];
    self.newestProvider.delegate = self;
    self.newestProvider.hasPaging = NO;
    self.newestProvider.hasRefreshControl = YES;
    [self.newestProvider bindWithTableView:self.newestTableView];
    self.newestProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        return [SHARE_NW_ENGINE feedingAggregationOnSucceed:succeedBlock onError:errorBlock];
    };
    [self _reloadCurrentProvider];
}

- (void)_configNav
{
    self.title = @"美搭榜单";
}

- (void)_reloadCurrentProvider {
    [self.newestProvider reloadData];
}

#pragma mark - CKCalendarDelegate
- (void)calendar:(CKCalendarView *)calendar didSelectDate:(NSDate *)date {
    NSDate* now = [NSDate date];
    if ([date compare:now] == NSOrderedAscending) {
        self.calendarContainerView.hidden = YES;
        self.calendarView.selectedDate = date;
        date = [QSDateUtil clearTimeFromDate:date];
        NSDate* toDate = [date dateByAddingTimeInterval:24 * 60 * 60];
        QSNewestHourViewController* vc = [[QSNewestHourViewController alloc] initWithFromDate:date toDate:toDate];
        [self.navigationController pushViewController:vc animated:YES];
    }
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
    NSDate* toDate = [date dateByAddingTimeInterval:60 * 60];
    QSNewestHourViewController* vc = [[QSNewestHourViewController alloc] initWithFromDate:date toDate:toDate];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)provider:(QSMatcherTableViewProvider*)provider didClickPeople:(NSDictionary*)peopleDict {
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDict];
    vc.navigationController.navigationBar.hidden = NO;
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)showLatestS24Vc {
    NSDate* date = [NSDate dateWithTimeIntervalSinceNow:-1];
    date = [QSDateUtil clearMinuteFromDate:date];
    QSNewestHourViewController* vc = [[QSNewestHourViewController alloc] initWithFromDate:date toDate:[date dateByAddingTimeInterval:60 * 60]];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - 
- (void)showCalendarBtn {
    if (!self.calendarBtn.hidden) {
        return;
    }
    self.calendarBtn.hidden = NO;
}
- (void)hideCalendarBtn {
    if (self.calendarBtn.hidden) {
        return;
    }
    self.calendarBtn.hidden = YES;
}

#pragma mark -
- (void)didReceiveUserLoginNoti:(NSNotification*)noti {
    [self.newestProvider reloadData];
}

#pragma mark - for Scroll View
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    UIScrollView* sv = self.newestTableView;
    if (sv.contentOffset.y != 0) {
        _backToTopbtn.hidden = NO;
    } else {
        _backToTopbtn.hidden = YES;
        [self showCalendarBtn];
    }
    
    if (self.fShouldTrackDraging) {
        [self _handleCalendarBtnVisible:scrollView.contentOffset];
    }
    
//    [self _updateRecordContentOffset:scrollView.contentOffset isBegin:false];
}
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    self.initOffset = scrollView.contentOffset;
    self.fShouldTrackDraging = YES;
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerat {
    if (!decelerat) {
        self.fShouldTrackDraging = NO;
    }
}
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    self.fShouldTrackDraging = NO;
}


- (void)_handleCalendarBtnVisible:(CGPoint)offset {
    if (offset.y == 0) {
        [self showCalendarBtn];
        return;
    }
    
    CGFloat screenHeight = self.newestTableView.bounds.size.height;
    CGFloat deltaOffset = offset.y - self.initOffset.y;
    if (deltaOffset < -screenHeight * 0.8) {
        [self showCalendarBtn];
        self.fShouldTrackDraging = NO;
    } else if (deltaOffset > screenHeight / 8) {
        [self hideCalendarBtn];
        self.fShouldTrackDraging = NO;
    }
}

@end
