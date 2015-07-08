//
//  QSU02UserDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSU01UserDetailViewController.h"
#import "QSRootContainerViewController.h"

#import "QSPeopleUtil.h"
#import "QSMetadataUtil.h"
#import "QSShowUtil.h"
#import "QSItemUtil.h"

#import "QSNetworkKit.h"
#import "QSUserManager.h"

#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"

#import "QSImageCollectionModel.h"
#import "QSRecommendationDateCellModel.h"

#import "QSMatchCollectionViewProvider.h"
#import "QSFavorTableViewProvider.h"


#import "QSDateUtil.h"


#define PAGE_ID @"U01 - 个人"

@interface QSU01UserDetailViewController ()
@property (strong, nonatomic) NSDictionary* userInfo;
@property (assign, nonatomic) BOOL isCurrentUser;
#pragma mark Provider
@property (strong,nonatomic) QSShowCollectionViewProvider *matchProvider;
@property (strong, nonatomic) QSImageCollectionViewProvider* recommendProvider;
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
    }
    return self;
}
- (id)initWithPeople:(NSDictionary*)peopleDict;
{
    self = [super initWithNibName:@"QSU01UserDetailViewController" bundle:nil];
    if (self) {
        self.isCurrentUser = NO;
        [self providerInit];
        self.userInfo = peopleDict;
    }
    return self;
}

- (void)providerInit
{
    __weak QSU01UserDetailViewController* weakSelf = self;
    
    
    //Matcher
//    self.matchProvider = [[QSMatchCollectionViewProvider alloc] init];

    self.matchProvider = [[QSShowCollectionViewProvider alloc] init];
    self.matchProvider.type = 2;
    //Recommend
    self.recommendProvider  = [[QSImageCollectionViewProvider alloc] init];

    self.recommendProvider.networkDataFinalHandlerBlock = ^(){
        NSMutableArray* resultArray = weakSelf.recommendProvider.resultArray;
        if (resultArray.count == 0) {
            return;
        }
        for (int i = 0; i + 1 < resultArray.count || i == 0; i++) {
            QSImageCollectionModel* currentModel = resultArray[i];
            QSImageCollectionModel* nextModel = nil;
            if (resultArray.count > 1) {
                nextModel = resultArray[i + 1];
            }

            
            if (i == 0 && currentModel.type != QSImageCollectionModelTypeDate) {
                QSImageCollectionModel* m = [[QSImageCollectionModel alloc] init];
                m.type = QSImageCollectionModelTypeDate;
                QSRecommendationDateCellModel* dateModel = [[QSRecommendationDateCellModel alloc] init];
                dateModel.date = [QSShowUtil getRecommendDate:currentModel.data];
                dateModel.desc = [QSShowUtil getRecommentDesc:currentModel.data];
                m.data = dateModel;
                [resultArray insertObject:m atIndex:0];
                continue;
            }
            if (currentModel.type == QSImageCollectionModelTypeShow && nextModel.type == QSImageCollectionModelTypeShow) {
                NSDate* curDate = [QSShowUtil getRecommendDate:currentModel.data];
                NSDate* nextDate = [QSShowUtil getRecommendDate:nextModel.data];
                if (curDate && nextDate && ![QSDateUtil date:curDate isTheSameDayWith:nextDate]) {
                    QSImageCollectionModel* m = [[QSImageCollectionModel alloc] init];
                    m.type = QSImageCollectionModelTypeDate;
                    QSRecommendationDateCellModel* dateModel = [[QSRecommendationDateCellModel alloc] init];
                    dateModel.date = nextDate;
                    dateModel.desc = [QSShowUtil getRecommentDesc:nextModel.data];
                    m.data = dateModel;
                    [resultArray insertObject:m atIndex:i + 1];
                }
            }
        }
    };
    
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
    self.backBtn.hidden = self.isCurrentUser;
    self.menuBtn.hidden = !self.isCurrentUser;
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    [self.badgeView.btnGroup triggerSelectType:QSBadgeButtonTypeMatcher];
    self.badgeView.followBtn.hidden = self.isCurrentUser;
    [self.badgeView.followBtn addTarget:self action:@selector(followBtnPressed:) forControlEvents:UIControlEventTouchUpInside];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [self updateViewWithList];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)updateViewWithList {
    if (self.isCurrentUser) {
        self.userInfo = [QSUserManager shareUserManager].userInfo;
    }

    [self.badgeView bindWithPeopleDict:self.userInfo];
    
    [self.matchProvider reloadData];
    [self.recommendProvider reloadData];
    [self.favorProvider reloadData];
    [self.followingProvider reloadData];
    [self.followerProvider reloadData];
    
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
//        return [SHARE_NW_ENGINE getRecommendationFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.matchProvider bindWithCollectionView:self.matcherCollectionView];
    self.matchProvider.delegate = self;
    [self.matchProvider reloadData];
    
    //Recommend
    self.recommendProvider.hasRefreshControl = NO;
    [self.recommendProvider bindWithCollectionView:self.recommendCollectionView];
    self.recommendProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getRecommendationFeedingPage:page onSucceed:^(NSArray *array, NSDictionary *metadata)
                {
                    NSMutableArray* mArray = [@[] mutableCopy];
                    for (NSDictionary* dict in array) {
                        QSImageCollectionModel* m = [[QSImageCollectionModel alloc] init];
                        m.type = QSImageCollectionModelTypeShow;
                        m.data = dict;
                        [mArray addObject:m];
                    }
                    succeedBlock(mArray, metadata);
                } onError:^(NSError* e){
                    errorBlock(e);
                }];
    };
    
    
    self.recommendProvider.filterBlock = ^BOOL(id obj){
        return [QSShowUtil getIsLike:obj];
    };
    self.recommendProvider.delegate = self;
    [self.recommendProvider reloadData];

    //Favor
    self.favorProvider.hasRefreshControl = NO;
    self.favorProvider.networkBlock =^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getLikeFeedingUser:[QSUserManager shareUserManager].userInfo page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
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
    self.followerProvider.delegate = self;
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
    self.followerTableView
    ];
}


#pragma mark - IBAction
- (void)updateView
{
    [self.badgeView bindWithPeopleDict:self.userInfo];

}

#pragma mark - QSImageCollectionViewProviderDelegate
- (void)didClickModel:(QSImageCollectionModel*)model
             provider:(QSImageCollectionViewProvider*)provider
{

    switch (model.type) {
        case QSImageCollectionModelTypeShow:
        {
            [self showShowDetailViewController:model.data];
            break;
        }
        case QSImageCollectionModelTypeItem:
        {
            [self showItemDetailViewController:model.data];
            break;
        }
        default:
            break;
    }
}
- (IBAction)menuBtnPressed:(id)sender {
    if ([self.menuProvider respondsToSelector:@selector(didClickMenuBtn)]) {
        [self.menuProvider didClickMenuBtn];
    }
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
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
        if (error.code == 1019) {
            self.badgeView.followBtn.selected = YES;
            [QSPeopleUtil setPeople:self.userInfo isFollowed:YES];
        } else if (error.code == 1020) {
            self.badgeView.followBtn.selected = NO;
            [QSPeopleUtil setPeople:self.userInfo isFollowed:NO];
        }
    }];
}

- (void)clickModel:(NSDictionary*)model {
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:model];
    vc.menuProvider = self.menuProvider;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    //NSLog(@"scrollView.con = %f",scrollView.contentOffset.y);
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
@end
