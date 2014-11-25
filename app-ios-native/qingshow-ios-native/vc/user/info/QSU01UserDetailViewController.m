//
//  QSU02UserDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSU01UserDetailViewController.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "QSU02UserSettingViewController.h"

@interface QSU01UserDetailViewController ()
@property (strong, nonatomic) NSDictionary* userInfo;
#pragma mark Delegate Obj
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* favorDelegate;
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
    self.favorDelegate  = [[QSShowCollectionViewDelegateObj alloc] init];
    self.recommendationDelegate = [[QSShowCollectionViewDelegateObj alloc] init];
    self.followingDelegate = [[QSModelListTableViewDelegateObj alloc] init];
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self configNavBar];
    [self configView];
    [self bindDelegateObj];
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
    [self.favorDelegate bindWithCollectionView:self.favorCollectionView];
#warning 需要换成正确的api
    self.favorDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.favorDelegate.delegate = self;
    [self.favorDelegate fetchDataOfPage:1];
    

    //recommendation collectioin view
    [self.recommendationDelegate bindWithCollectionView:self.recommendationCollectionView];
#warning 需要换成正确的api
    self.recommendationDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.recommendationDelegate.delegate = self;
    [self.recommendationDelegate fetchDataOfPage:1];

    
    //following table view
    [self.followingDelegate bindWithTableView:self.followingTableView];
#warning 需要换成正确的api
    self.followingDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getModelListPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.followingDelegate.delegate = self;
    [self.followingDelegate fetchDataOfPage:1];
}

- (void)configView
{
    //title
    self.title = self.userInfo[@"name"];
    [self.badgeView bindWithPeopleDict:self.userInfo];
    
    //Show and Hide
    self.viewArray = @[self.favorCollectionView, self.recommendationCollectionView,self.followingTableView];
    
    self.favorCollectionView.hidden = NO;
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

#pragma mark -
- (void)clickModel:(NSDictionary*)model
{
    
}
- (void)followBtnPressed:(NSDictionary*)model
{
    [SHARE_NW_ENGINE handleFollowModel:model onSucceed:^(BOOL fFollow) {
        if (fFollow) {
            [self showTextHud:@"follow succeed"];
        }
        else
        {
            [self showTextHud:@"unfollow succeed"];
        }
    } onError:^(NSError *error) {
        [self showErrorHudWithText:@"error"];
    }];
}
@end
