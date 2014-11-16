//
//  QSP02ModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP02ModelDetailViewController.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"


@interface QSP02ModelDetailViewController ()

#pragma mark - Data
@property (strong, nonatomic) NSMutableDictionary* peopleDict;

#pragma mark - Delegate Obj
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* showsDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followingDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followerDelegate;


@end

@implementation QSP02ModelDetailViewController

#pragma mark - Init
- (id)initWithModel:(NSDictionary*)peopleDict
{
    self = [self initWithNibName:@"QSDetailBaseViewController" bundle:nil];
    if (self)
    {
        self.peopleDict = [peopleDict mutableCopy];
        
        [self delegateObjInit];
    }
    return self;
}

- (void)delegateObjInit
{
    self.showsDelegate = [[QSShowCollectionViewDelegateObj alloc] init];
    self.followingDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    self.followerDelegate = [[QSModelListTableViewDelegateObj alloc] init];
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self configView];
    [self bindDelegateObj];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/
#pragma mark - View
- (void)bindDelegateObj
{
    //following table view
    [self.followingDelegate bindWithTableView:self.followingTableView];
#warning 需要换成正确的api
    self.followingDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getModelListPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.followingDelegate.delegate = self;
    [self.followingDelegate fetchDataOfPage:1];
    
    //follower table view
    [self.followerDelegate bindWithTableView:self.followerTableView];
#warning 需要换成正确的api
    self.followerDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getModelListPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.followerDelegate.delegate = self;
    [self.followerDelegate fetchDataOfPage:1];
    
#warning 需要换成正确的api
    //Show collectioin view
    [self.showsDelegate bindWithCollectionView:self.showCollectionView];
    __weak QSP02ModelDetailViewController* weakSelf = self;
    
    self.showsDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getFeedByModel:weakSelf.peopleDict[@"_id"] page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.showsDelegate.delegate = self;
    [self.showsDelegate fetchDataOfPage:1];

}

- (void)configView
{
    //title
    self.title = self.peopleDict[@"name"];
    [self.badgeView bindWithPeopleDict:self.peopleDict];

    //Show and Hide
    self.viewArray = @[self.showCollectionView, self.followingTableView, self.followerTableView];
    
    self.showCollectionView.hidden = NO;
    self.followerTableView.hidden = YES;
    self.followingTableView.hidden = YES;

    //Section title
    NSArray* titleArray = @[@"搭配",@"关注",@"粉丝"];
    for (int i = 0; i < 3; i++) {
        [self.badgeView.btnGroup setNumber:@(0).stringValue atIndex:i];
        [self.badgeView.btnGroup setTitle:titleArray[i] atIndex:i];
    }
}


- (void)singleButtonPressed
{
    NSNumber* hasFollowed = self.peopleDict[@"hasFollowed"];
    if (hasFollowed && hasFollowed.boolValue) {
        [SHARE_NW_ENGINE unfollowPeople:self.peopleDict[@"_id"] onSucceed:^{
            [self showTextHud:@"unfollow successfully"];
            self.peopleDict[@"hasFollowed"] = @NO;
            [self.badgeView bindWithPeopleDict:self.peopleDict];
        } onError:^(NSError *error) {
            [self showTextHud:@"error"];
        }];
    }
    else {
        [SHARE_NW_ENGINE followPeople:self.peopleDict[@"_id"] onSucceed:^{
            [self showTextHud:@"follow successfully"];
            self.peopleDict[@"hasFollowed"] = @YES;
            [self.badgeView bindWithPeopleDict:self.peopleDict];
        } onError:^(NSError *error) {
            [self showTextHud:@"error"];
        }];
    }
    
}


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
