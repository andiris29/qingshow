//Ø
//  QSU02UserDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSU01UserDetailViewController.h"
#import "QSU02UserSettingViewController.h"
#import "QSRootContainerViewController.h"


#import "QSPeopleUtil.h"
#import "QSMetadataUtil.h"
#import "QSShowUtil.h"
#import "QSItemUtil.h"
#import "QSEntityUtil.h"

#import "QSNetworkKit.h"
#import "QSUserManager.h"

#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"

#import "QSImageCollectionModel.h"
#import "QSRecommendationDateCellModel.h"

#import "QSS03ShowDetailViewController.h"
#import "QSU15BonusViewController.h"

#import "QSUnreadManager.h"

#import "QSDateUtil.h"
#import "QSUnreadManager.h"


#define PAGE_ID @"U01 - 个人"

@interface QSU01UserDetailViewController () <QSShowProviderDelegate, QSPeoplelListTableViewProviderDelegate>
@property (strong, nonatomic) NSDictionary* userInfo;
@property (assign, nonatomic) BOOL isCurrentUser;
@property (assign, nonatomic) BOOL showMenuIcon;

#pragma mark Provider
@property (strong,nonatomic) QSShowCollectionViewProvider *matchProvider;
@property (strong,nonatomic) QSShowCollectionViewProvider *recommendProvider;
@property (strong, nonatomic) QSShowCollectionViewProvider *favorProvider;
@property (strong, nonatomic) QSPeopleListTableViewProvider* followingProvider;
@property (strong, nonatomic) QSPeopleListTableViewProvider* followerProvider;

@end

@implementation QSU01UserDetailViewController

#pragma mark - Init
- (id)initWithCurrentUser
{
    self = [self initWithPeople:[QSUserManager shareUserManager].userInfo];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveCurrentUserInfoUpdate:) name:kUserInfoUpdateNotification object:nil];
        self.isCurrentUser = YES;
        self.showMenuIcon = YES;
    }
    return self;
}
- (id)initWithPeople:(NSDictionary*)peopleDict;
{
    self = [super initWithNibName:@"QSU01UserDetailViewController" bundle:nil];
    if (self) {
        self.isCurrentUser = [[QSEntityUtil getIdOrEmptyStr:[QSUserManager shareUserManager].userInfo] isEqualToString:[QSEntityUtil getIdOrEmptyStr:peopleDict]];
        self.showMenuIcon = NO;
        [self providerInit];
        self.userInfo = peopleDict;
    }
    return self;
}

- (void)providerInit
{
    self.matchProvider = [[QSShowCollectionViewProvider alloc] init];
    //Recommend
    self.recommendProvider  = [[QSShowCollectionViewProvider alloc] init];
    
    //Favor
    self.favorProvider = [[QSShowCollectionViewProvider alloc] init];
    //Following
    self.followingProvider = [[QSPeopleListTableViewProvider alloc] init];
    //Follower
    self.followerProvider = [[QSPeopleListTableViewProvider alloc] init];
    
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self configView];
    [self bindProvider];
    self.backToTopBtn.hidden = YES;
    self.backBtn.hidden = self.showMenuIcon;
    self.menuBtn.hidden = !self.showMenuIcon;
    self.settingBtn.hidden = self.menuBtn.hidden;
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    [self.badgeView.btnGroup triggerSelectType:QSBadgeButtonTypeMatcher];
    self.badgeView.followBtn.hidden = self.isCurrentUser;
    self.badgeView.bonusBtn.hidden = !self.isCurrentUser;
    [self.badgeView.followBtn addTarget:self action:@selector(followBtnPressed:) forControlEvents:UIControlEventTouchUpInside];
    [self.badgeView.bonusBtn addTarget:self action:@selector(bonusBtnPressed:) forControlEvents:UIControlEventTouchUpInside];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleUnreadChange:) name:kQSUnreadChangeNotificationName object:nil];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self updateMenuDot];
    self.navigationController.navigationBarHidden = YES;
    [self updateViewWithList];
    [MobClick beginLogPageView:PAGE_ID];
    [SHARE_NW_ENGINE queryPeopleDetail:[QSEntityUtil getIdOrEmptyStr:self.userInfo] onSucceed:^(NSDictionary * p) {
        if (p) {
            self.userInfo = p;
            [self.badgeView bindWithPeopleDict:self.userInfo];
        }
    } onError:nil];
}

- (void)updateViewWithList {
    if (self.isCurrentUser) {
        self.userInfo = [QSUserManager shareUserManager].userInfo;
    }

    [self.badgeView bindWithPeopleDict:self.userInfo];
    
    for (QSAbstractListViewProvider* provider in @[self.matchProvider, self.recommendProvider, self.favorProvider, self.followingProvider, self.followerProvider]) {
        [provider reloadData];
    }
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.recommendProvider fetchDataOfPage:1];
    [MobClick endLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    for (QSAbstractListViewProvider* provider in @[self.matchProvider, self.recommendProvider, self.favorProvider, self.followingProvider, self.followerProvider]) {
        [provider cancelImageLoading];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - View
- (void)bindProvider
{
    __weak QSU01UserDetailViewController* weakSelf = self;
    
    //Matcher
    self.matchProvider.hasRefreshControl = NO;
    self.matchProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE feedingMatchCreateBy:weakSelf.userInfo page:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.matchProvider bindWithCollectionView:self.matcherCollectionView];
    self.matchProvider.delegate = self;
    [self.matchProvider reloadData];
    
    //Recommend
    self.recommendProvider.hasRefreshControl = NO;
    [self.recommendProvider bindWithCollectionView:self.recommendCollectionView];
//    self.recommendProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
//        return [SHARE_NW_ENGINE getRecommendationFeedingPage:page onSucceed:^(NSArray *array, NSDictionary *metadata)
//                {
//                    NSMutableArray* mArray = [@[] mutableCopy];
//                    for (NSDictionary* dict in array) {
//                        QSImageCollectionModel* m = [[QSImageCollectionModel alloc] init];
//                        m.type = QSImageCollectionModelTypeShow;
//                        m.data = dict;
//                        [mArray addObject:m];
//                    }
//                    succeedBlock(mArray, metadata);
//                } onError:^(NSError* e){
//                    errorBlock(e);
//                }];
//    };
    self.recommendProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        return [SHARE_NW_ENGINE getRecommendationFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    
    self.recommendProvider.delegate = self;
    [self.recommendProvider reloadData];

    //Favor
    self.favorProvider.hasRefreshControl = NO;
    self.favorProvider.networkBlock =^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getLikeFeedingUser:weakSelf.userInfo page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            succeedBlock(array, metadata);
        } onError:^(NSError *error) {
            errorBlock(error);
        }];
    };
    [self.favorProvider bindWithCollectionView:self.favorCollectionView];
    self.favorProvider.delegate = self;
    [self.favorProvider reloadData];
    
    //Following
    self.followingProvider.hasRefreshControl = NO;
    self.followingProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollowed:weakSelf.userInfo page:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.followingProvider bindWithTableView:self.followingTableView];
    [self.followingProvider reloadData];
    self.followingProvider.delegate = self;
    //Follower
    self.followerProvider.hasRefreshControl = NO;
    self.followerProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollower:weakSelf.userInfo page:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.followerProvider bindWithTableView:self.followerTableView];
    [self.followerProvider reloadData];
    self.followerProvider.delegate = self;
}

- (void)configView
{
    //title
    self.title = [QSPeopleUtil getNickname:self.userInfo];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:[UIFont systemFontOfSize:18],
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    
    [self updateView];
    
    //Show and Hide
    self.viewArray =
  @[
    self.matcherCollectionView,
    self.recommendCollectionView,
    self.favorCollectionView,
    self.followingTableView,
    self.followerTableView,
    self.backToTopBtn
    ];
}


#pragma mark - IBAction
- (void)updateView
{
    [self.badgeView bindWithPeopleDict:self.userInfo];

}
- (IBAction)settingBtnPressed:(id)sender {
   UIViewController* vc = [[QSU02UserSettingViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)menuBtnPressed:(id)sender {
    [QSRootNotificationHelper postShowRootMenuNoti];
}
- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveCurrentUserInfoUpdate:(NSNotification*)noti{
    [self updateViewWithList];
}

- (void)followBtnPressed:(id)sender {
    [SHARE_NW_ENGINE handleFollowModel:self.userInfo onSucceed:^(BOOL f) {
        self.badgeView.followBtn.selected = f;
        [self.followerProvider reloadData];
    } onError:^(NSError *error) {
        [self handleError:error];
        if (error.code == 1019) {
            self.badgeView.followBtn.selected = YES;
            [QSPeopleUtil setPeople:self.userInfo isFollowed:YES];
        } else if (error.code == 1020) {
            self.badgeView.followBtn.selected = NO;
            [QSPeopleUtil setPeople:self.userInfo isFollowed:NO];
        }
    }];
}

- (void)bonusBtnPressed:(id)sender {
    [self showBonusVC];
}


- (void)showBonusVC
{
    NSDictionary *dic = [QSUserManager shareUserManager].userInfo;
    QSU15BonusViewController *vc = [[QSU15BonusViewController alloc] init];
    vc.peopleId = [QSPeopleUtil getPeopleId:dic];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)clickModel:(NSDictionary*)model {
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:model];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [super scrollViewDidScroll:scrollView];
    if (scrollView.contentOffset.y != -360) {
        _backToTopBtn.hidden = NO;
    }
    else
    {
        _backToTopBtn.hidden = YES;
    }
}
- (IBAction)topToTopBtnPressed:(id)sender {
    UIScrollView *scrollView = self.viewArray[self.currentSection];
    CGPoint p = [scrollView contentOffset];
    p.y = -360;
    [scrollView setContentOffset:p animated:YES];
    _backToTopBtn.hidden = YES;
}

- (void)didClickShow:(NSDictionary*)showDict provider:(QSAbstractListViewProvider *)provider
{
    QSS03ShowDetailViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    vc.showDeletedBtn = provider == self.matchProvider && self.isCurrentUser;
    [self.navigationController pushViewController:vc animated:YES];
    
}

- (void)didClickPeople:(NSDictionary*)peopleDict provider:(QSAbstractListViewProvider*)provider
{
//    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:sender];
//    vc.navigationController.navigationBar.hidden = NO;
//    [self.navigationController pushViewController:vc animated:YES];
    
}

- (void)updateMenuDot {
    if ([[QSUnreadManager getInstance] shouldShowDotAtMenu]) {
        [self.menuBtn setImage:[UIImage imageNamed:@"nav_btn_menu_new"] forState:UIControlStateNormal];
    } else {
        [self.menuBtn setImage:[UIImage imageNamed:@"nav_menu_black"] forState:UIControlStateNormal];
    }
}
- (void)handleUnreadChange:(NSNotification*)noti {
    [self updateMenuDot];
}

- (void)btnGroup:(QSBadgeBtnGroup*)btnGroup didSelectType:(QSBadgeButtonType)type {
    [super btnGroup:btnGroup didSelectType:type];
    if (type == QSBadgeButtonTypeRecommend) {
        [[QSUnreadManager getInstance] clearRecommandUnread];
    }
}

@end
