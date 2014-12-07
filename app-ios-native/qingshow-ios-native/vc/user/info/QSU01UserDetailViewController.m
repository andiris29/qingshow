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

#import "QSNetworkKit.h"
#import "QSUserManager.h"

#import "UIViewController+ShowHud.h"

@interface QSU01UserDetailViewController ()
@property (strong, nonatomic) NSDictionary* userInfo;
#pragma mark Delegate Obj
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* likedDelegate;
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* recommendationDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followingDelegate;

@end

@implementation QSU01UserDetailViewController
#pragma mark - Init
- (id)init
{
    self = [super initWithNibName:@"QSU01UserDetailViewController" bundle:nil];
    if (self) {
        [self delegateObjInit];
        self.userInfo = [QSUserManager shareUserManager].userInfo;
        self.type = QSSectionButtonGroupTypeThree;
    }
    return self;
}

- (void)delegateObjInit
{
    self.likedDelegate  = [[QSShowCollectionViewDelegateObj alloc] init];
    self.likedDelegate.delegate = self;
    self.recommendationDelegate = [[QSShowCollectionViewDelegateObj alloc] init];
    self.recommendationDelegate.delegate = self;
    self.followingDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    self.followingDelegate.delegate = self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self configNavBar];
    [self configView];
    [self bindDelegateObj];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.userInfo = [QSUserManager shareUserManager].userInfo;
    [self.badgeView bindWithPeopleDict:self.userInfo];
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
        } onError:errorBlock];

    };
    self.likedDelegate.delegate = self;
    [self.likedDelegate reloadData];

    //recommendation collectioin view
    [self.recommendationDelegate bindWithCollectionView:self.recommendationCollectionView];
    self.recommendationDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getRecommendationFeedingPage:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:1];
            succeedBlock(array, metadata);
        } onError:errorBlock];
        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.recommendationDelegate.delegate = self;
    [self.recommendationDelegate reloadData];
    
    //following table view
    [self.followingDelegate bindWithTableView:self.followingTableView];
    self.followingDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollowed:weakSelf.userInfo page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:2];
            succeedBlock(array, metadata);
        } onError:errorBlock];
    };
    self.followingDelegate.delegate = self;
    [self.followingDelegate reloadData];
}

- (void)configView
{
    //title
    self.title = self.userInfo[@"name"];
    [self updateView];
    
    //Show and Hide
    self.viewArray = @[self.likedCollectionView, self.recommendationCollectionView,self.followingTableView];
    
    self.likedCollectionView.hidden = NO;
    self.recommendationCollectionView.hidden = YES;
    self.followingTableView.hidden = YES;
    
    //Section title
    NSArray* titleArray = @[@"收藏",@"推荐",@"关注"];
    for (int i = 0; i < 3; i++) {
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
- (void)accountButtonPressed
{
    UIStoryboard *tableViewStoryboard = [UIStoryboard storyboardWithName:@"QSU02UserSetting" bundle:nil];
    QSU02UserSettingViewController *vc = [tableViewStoryboard instantiateViewControllerWithIdentifier:@"U02UserSetting"];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)updateView
{
    [self.badgeView bindWithPeopleDict:self.userInfo];

}
@end
