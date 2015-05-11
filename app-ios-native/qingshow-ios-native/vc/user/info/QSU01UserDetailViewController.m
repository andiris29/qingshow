//
//  QSU02UserDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSU01UserDetailViewController.h"
#import "QSU02UserSettingViewController.h"

#import "QSPeopleUtil.h"
#import "QSMetadataUtil.h"
#import "QSShowUtil.h"

#import "QSNetworkKit.h"
#import "QSUserManager.h"

#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"

#import "QSBrandUtil.h"
#import "QSImageCollectionModel.h"
#import "QSRecommendationDateCellModel.h"

#import "QSDateUtil.h"

#define PAGE_ID @"U01 - 个人"

@interface QSU01UserDetailViewController ()
@property (strong, nonatomic) NSDictionary* userInfo;

#pragma mark Provider
@property (strong, nonatomic) QSImageCollectionViewProvider* likedProvider;
@property (strong, nonatomic) QSImageCollectionViewProvider* recommendationProvider;
@property (assign, nonatomic) BOOL fShowAccountBtn;
@end

@implementation QSU01UserDetailViewController
#pragma mark - Init
- (id)initWithCurrentUser
{
    self = [self initWithPeople:[QSUserManager shareUserManager].userInfo];
    if (self) {
        self.fShowAccountBtn = YES;
    }
    return self;
}
- (id)initWithPeople:(NSDictionary*)peopleDict;
{
    self = [super initWithNibName:@"QSU01UserDetailViewController" bundle:nil];
    if (self) {
        [self providerInit];
        self.userInfo = peopleDict;
        self.type = QSSectionButtonGroupTypeU01;
        self.fShowAccountBtn = NO;
    }
    return self;
}

- (void)providerInit
{
    __weak QSU01UserDetailViewController* weakSelf = self;
    
    self.likedProvider  = [[QSImageCollectionViewProvider alloc] init];
    self.likedProvider.delegate = self;
    self.likedProvider.hasRefreshControl = NO;
    self.recommendationProvider = [[QSImageCollectionViewProvider alloc] init];
    self.recommendationProvider.delegate = self;
    self.recommendationProvider.networkDataFinalHandlerBlock = ^(){
        NSMutableArray* resultArray = weakSelf.recommendationProvider.resultArray;
        for (int i = 0; i < resultArray.count - 1; i++) {
            QSImageCollectionModel* currentModel = resultArray[i];
            QSImageCollectionModel* nextModel = resultArray[i + 1];
            
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

    
//    self.recommendationProvider.delegate = self;
    self.recommendationProvider.hasRefreshControl = NO;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    //[self configNavBar];
    [self configView];
    [self bindDelegateObj];
    self.accountBtn.hidden = !self.fShowAccountBtn;
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (self.fShowAccountBtn) {
        self.userInfo = [QSUserManager shareUserManager].userInfo;
    }
    [self.badgeView bindWithPeopleDict:self.userInfo];
    
    [self.likedProvider refreshClickedData];
    [self.recommendationProvider refreshClickedData];
//    [self.followingDelegate refreshClickedData];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.likedProvider fetchDataOfPage:1];
    [self.recommendationProvider fetchDataOfPage:1];
//    [self.followingDelegate fetchDataOfPage:1];
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

#pragma mark - View
- (void)bindDelegateObj
{
    __weak QSU01UserDetailViewController* weakSelf = self;
    
    //favor collectioin view
    [self.likedProvider bindWithCollectionView:self.likedCollectionView];
    self.likedProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getLikeFeedingUser:weakSelf.userInfo page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
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
    if (self.fShowAccountBtn) {
        self.likedProvider.filterBlock = ^BOOL(id obj){
            return [QSShowUtil getIsLike:obj];
        };
    }

    self.likedProvider.delegate = self;
    [self.likedProvider reloadData];

    //recommendation collectioin view
    [self.recommendationProvider bindWithCollectionView:self.recommendationCollectionView];
    self.recommendationProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
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
    self.recommendationProvider.delegate = self;
    [self.recommendationProvider reloadData];

}

- (void)configView
{
    //title
    self.title = self.userInfo[@"name"];
    [self updateView];
    
    //Show and Hide
    self.viewArray = @[self.recommendationCollectionView, self.likedCollectionView];
    
    self.recommendationCollectionView.hidden = NO;
    self.likedCollectionView.hidden = YES;

    
    //Section title
    NSArray* titleArray = @[@"搭配推荐",@"我的收藏"];
    for (int i = 0; i < titleArray.count; i++) {
        [self.badgeView.btnGroup setTitle:titleArray[i] atIndex:i];
    }
}


- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    UIBarButtonItem* rightButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
    self.navigationItem.rightBarButtonItem = rightButtonItem;
}

#pragma mark - IBAction
- (IBAction)accountButtonPressed
{
    UIStoryboard *tableViewStoryboard = [UIStoryboard storyboardWithName:@"QSU02UserSetting" bundle:nil];
    QSU02UserSettingViewController *vc = [tableViewStoryboard instantiateViewControllerWithIdentifier:@"U02UserSetting"];
    [self.navigationController pushViewController:vc animated:YES];
}

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
@end
