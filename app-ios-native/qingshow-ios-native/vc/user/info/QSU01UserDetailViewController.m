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

#import "QSDateUtil.h"
//#import "QSU13PersonalizeViewController.h"

#define PAGE_ID @"U01 - 个人"

@interface QSU01UserDetailViewController ()
@property (strong, nonatomic) NSDictionary* userInfo;

#pragma mark Provider
@property (strong, nonatomic) QSImageCollectionViewProvider* recommendProvider;
@end

@implementation QSU01UserDetailViewController
#pragma mark - Init
- (id)initWithCurrentUser
{
    self = [self initWithPeople:[QSUserManager shareUserManager].userInfo];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveCurrentUserInfoUpdate:) name:kUserInfoUpdateNotification object:nil];
    }
    return self;
}
- (id)initWithPeople:(NSDictionary*)peopleDict;
{
    self = [super initWithNibName:@"QSU01UserDetailViewController" bundle:nil];
    if (self) {
        [self providerInit];
        self.userInfo = peopleDict;
    }
    return self;
}

- (void)providerInit
{
    __weak QSU01UserDetailViewController* weakSelf = self;
    
    self.recommendProvider  = [[QSImageCollectionViewProvider alloc] init];
    self.recommendProvider.delegate = self;
    self.recommendProvider.hasRefreshControl = NO;
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
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self configView];
    [self bindDelegateObj];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];

}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [self updateViewWithList];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)updateViewWithList {
    self.userInfo = [QSUserManager shareUserManager].userInfo;
    [self.badgeView bindWithPeopleDict:self.userInfo];
    
    
    [self.recommendProvider refreshClickedData];
    
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
- (void)bindDelegateObj
{
    //favor collectioin view
    [self.recommendProvider bindWithCollectionView:self.likedCollectionView];
    self.recommendProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
//        return [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata)
//                {
//                    NSMutableArray* mArray = [@[] mutableCopy];
//                    for (NSDictionary* dict in array) {
//                        QSImageCollectionModel* m = [[QSImageCollectionModel alloc] init];
//                        m.type = QSImageCollectionModelTypeShow;
//                        m.data = dict;
//                        [mArray addObject:m];
//                    }
//                    succeedBlock(mArray, metadata);
//                } onError:errorBlock];
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
    self.viewArray = @[self.likedCollectionView];
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

- (void)didReceiveCurrentUserInfoUpdate:(NSNotification*)noti{
    [self updateViewWithList];
}
@end
