//
//  QSU02UserDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSU01UserDetailViewController.h"
#import "QSU02UserSettingViewController.h"
#import "QSP04BrandDetailViewController.h"

#import "QSPeopleUtil.h"
#import "QSMetadataUtil.h"
#import "QSShowUtil.h"

#import "QSNetworkKit.h"
#import "QSUserManager.h"

#import "UIViewController+ShowHud.h"
#import "QSBrandUtil.h"

@interface QSU01UserDetailViewController ()
@property (strong, nonatomic) NSDictionary* userInfo;
#pragma mark Delegate Obj
@property (strong, nonatomic) QSShowCollectionViewProvider* likedDelegate;
@property (strong, nonatomic) QSShowCollectionViewProvider* recommendationDelegate;
@property (strong, nonatomic) QSModelListTableViewProvider* followingDelegate;
@property (strong, nonatomic) QSBigImageTableViewProvider* likedBrandDelegate;
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
        [self delegateObjInit];
        self.userInfo = peopleDict;
        self.type = QSSectionButtonGroupTypeU01;
        self.fShowAccountBtn = NO;
    }
    return self;
}

- (void)delegateObjInit
{
    self.likedDelegate  = [[QSShowCollectionViewProvider alloc] init];
    self.likedDelegate.delegate = self;
    self.likedDelegate.hasRefreshControl = NO;
    self.recommendationDelegate = [[QSShowCollectionViewProvider alloc] init];
    self.recommendationDelegate.delegate = self;
    self.recommendationDelegate.hasRefreshControl = NO;
    self.followingDelegate = [[QSModelListTableViewProvider alloc] init];
    self.followingDelegate.delegate = self;
    self.followingDelegate.hasRefreshControl = NO;
    self.likedBrandDelegate = [[QSBigImageTableViewProvider alloc] init];
    self.likedBrandDelegate.type= QSBigImageTableViewCellTypeBrand;
    self.likedBrandDelegate.hasRefreshControl = NO;
    self.likedBrandDelegate.delegate = self;
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
    
    [self.likedDelegate refreshClickedData];
    [self.recommendationDelegate refreshClickedData];
    [self.followingDelegate refreshClickedData];
    [self.likedBrandDelegate refreshClickedData];
}
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.likedDelegate fetchDataOfPage:1];
    [self.recommendationDelegate fetchDataOfPage:1];
    [self.followingDelegate fetchDataOfPage:1];
    [self.likedBrandDelegate fetchDataOfPage:1];
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
    [self.likedDelegate bindWithCollectionView:self.likedCollectionView];
    self.likedDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getLikeFeedingPage:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:0];
            succeedBlock(array, metadata);
        } onError:^(NSError* e){
            if (page == 1) {
                [weakSelf.badgeView.btnGroup setNumber:@"0" atIndex:0];
            }
            errorBlock(e);
        }];

    };
    if (self.fShowAccountBtn) {
        self.likedDelegate.filterBlock = ^BOOL(id obj){
            return [QSShowUtil getIsLike:obj];
        };
    }

    self.likedDelegate.delegate = self;
    [self.likedDelegate reloadData];

    //recommendation collectioin view
    [self.recommendationDelegate bindWithCollectionView:self.recommendationCollectionView];
    self.recommendationDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getRecommendationFeedingPage:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:1];
            succeedBlock(array, metadata);
        } onError:^(NSError* e){
            if (page == 1) {
                [weakSelf.badgeView.btnGroup setNumber:@"0" atIndex:1];
            }
            errorBlock(e);
        }];
    };
    self.recommendationDelegate.delegate = self;
    [self.recommendationDelegate reloadData];
    
    //following table view
    [self.followingDelegate bindWithTableView:self.followingTableView];
    self.followingDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollowed:weakSelf.userInfo page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:2];
            succeedBlock(array, metadata);
        } onError:^(NSError* e){
            if (page == 1) {
                [weakSelf.badgeView.btnGroup setNumber:@"0" atIndex:2];
            }
            errorBlock(e);
        }];
    };
    if (self.fShowAccountBtn) {
        self.followingDelegate.filterBlock = ^BOOL(id obj){
            return [QSPeopleUtil  getPeopleIsFollowed:obj];
        };
    }

    self.followingDelegate.delegate = self;
    [self.followingDelegate reloadData];
    //Like brand tableVIew
    [self.likedBrandDelegate bindWithTableView:self.likeBrandTableView];
    self.likedBrandDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollowedBrand:weakSelf.userInfo page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:3];
            succeedBlock(array, metadata);
        } onError:^(NSError* e){
            if (page == 1) {
                [weakSelf.badgeView.btnGroup setNumber:@"0" atIndex:3];
            }
            errorBlock(e);
        }];
    };
    if (self.fShowAccountBtn) {
        self.likedBrandDelegate.filterBlock = ^BOOL(id obj) {
            return [QSBrandUtil getHasFollowBrand:obj];
        };
    }

    [self.likedBrandDelegate reloadData];
}

- (void)configView
{
    //title
    self.title = self.userInfo[@"name"];
    [self updateView];
    
    //Show and Hide
    self.viewArray = @[self.likedCollectionView, self.recommendationCollectionView,self.followingTableView, self.likeBrandTableView];
    
    self.likedCollectionView.hidden = NO;
    self.recommendationCollectionView.hidden = YES;
    self.followingTableView.hidden = YES;
    
    //Section title
    NSArray* titleArray = @[@"收藏",@"推荐",@"关注", @"店铺"];
    for (int i = 0; i < titleArray.count; i++) {
        [self.badgeView.btnGroup setNumber:@(0).stringValue atIndex:i];
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

- (void)followBtnPressed:(NSDictionary*)model
{
    [SHARE_NW_ENGINE handleFollowModel:model onSucceed:^(BOOL fFollow) {
        if (fFollow) {
            [self showTextHud:@"关注成功"];
            [self.followingDelegate refreshData:model];
        }
        else {
            if (self.fShowAccountBtn) {
                [self.followingDelegate removeData:model withAnimation:YES];
            } else {
                [self.followingDelegate refreshData:model];
            }
            [self showTextHud:@"取消关注成功"];
            [self.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:self.followingDelegate.metadataDict] atIndex:2];
        }
    } onError:^(NSError *error) {
        [self showErrorHudWithText:@"error"];
    }];
}
- (void)clickDetailOfDict:(NSDictionary *)dict type:(QSBigImageTableViewCellType)type
{
    if (type == QSBigImageTableViewCellTypeBrand) {
        UIViewController* vc = [[QSP04BrandDetailViewController alloc] initWithBrand:dict];
        [self.navigationController pushViewController:vc animated:YES];
    }
}
@end
