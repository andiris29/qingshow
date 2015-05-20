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
#import "QSU13PersonalizeViewController.h"

#define PAGE_ID @"U01 - 个人"

@interface QSU01UserDetailViewController ()
@property (strong, nonatomic) NSDictionary* userInfo;

#pragma mark Provider
@property (strong, nonatomic) QSImageCollectionViewProvider* likedProvider;
@property (strong, nonatomic) QSImageCollectionViewProvider* recommendationProvider;
@end

@implementation QSU01UserDetailViewController
#pragma mark - Init
- (id)initWithCurrentUser
{
    self = [self initWithPeople:[QSUserManager shareUserManager].userInfo];
    if (self) {
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
    [self configView];
    [self bindDelegateObj];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    self.userInfo = [QSUserManager shareUserManager].userInfo;
    [self.badgeView bindWithPeopleDict:self.userInfo];
    

    [self.likedProvider refreshClickedData];
    [self.recommendationProvider refreshClickedData];
    [MobClick beginLogPageView:PAGE_ID];
    
    if (![QSPeopleUtil hasPersonalizeData:[QSUserManager shareUserManager].userInfo]) {
        [self.navigationController pushViewController:[[QSU13PersonalizeViewController alloc] init] animated:YES];
    }
    
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
    self.likedProvider.hasPaging = NO;
    self.likedProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        NSMutableArray* mArray = [@[] mutableCopy];

        __block int count = 0;
        __block NSError* outerError;
        __block void (^completeHandler)(NSError*) = ^void(NSError* err){
            if (count < 1) {
                count++;
                outerError = err;
                return;
            }
            if (!mArray.count && outerError && err) {
                errorBlock(err);
            } else {
                [mArray sortUsingComparator:^NSComparisonResult(QSImageCollectionModel* obj1, QSImageCollectionModel* obj2) {
                    NSDate* (^getDate)(QSImageCollectionModel*) = ^NSDate*(QSImageCollectionModel* m){
                        if (m.type == QSImageCollectionModelTypeShow) {
                            return [QSShowUtil getLikeDate:m.data];
                        } else if (m.type == QSImageCollectionModelTypeItem) {
                            return [QSItemUtil getLikeDate:m.data];
                        }
                        return nil;
                    };
                    NSDate* date1 = getDate(obj1);
                    NSDate* date2 = getDate(obj2);
                    return [date2 compare:date1];
                }];
                
                succeedBlock(mArray, nil);
            }
        };
        
        [SHARE_NW_ENGINE
         getLikeFeedingUser:weakSelf.userInfo
         page:page
         onSucceed:^(NSArray *array, NSDictionary *metadata) {
             for (NSDictionary* dict in array) {
                 QSImageCollectionModel* m = [[QSImageCollectionModel alloc] init];
                 m.type = QSImageCollectionModelTypeShow;
                 m.data = dict;
                 [mArray addObject:m];
             }
             completeHandler(nil);
         }
         onError:^(NSError* e){
             completeHandler(e);
         }];
        
        return [SHARE_NW_ENGINE
         getItemFeedingLikePage:page
         onSucceed:^(NSArray *array, NSDictionary *metadata) {
             for (NSDictionary* dict in array) {
                 QSImageCollectionModel* m = [[QSImageCollectionModel alloc] init];
                 m.type = QSImageCollectionModelTypeItem;
                 m.data = dict;
                 [mArray addObject:m];
             }
             completeHandler(nil);
         }
         onError:^(NSError* e){
             completeHandler(e);
         }];
    };
    
    self.likedProvider.filterBlock = ^BOOL(id obj){
        return [QSShowUtil getIsLike:obj];
    };
    

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
    self.title = [QSPeopleUtil getNickname:self.userInfo];
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
@end
