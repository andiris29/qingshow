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
#import "QSPeopleUtil.h"


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
    self.showsDelegate.delegate = self;
    self.followingDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    self.followingDelegate.delegate = self;
    self.followerDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    self.followerDelegate.delegate = self;
    self.followerDelegate.type = QSModelListTableViewDelegateObjTypeHideFollow;
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
    __weak QSP02ModelDetailViewController* weakSelf = self;
    self.followingDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollowed:weakSelf.peopleDict page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.followingDelegate.delegate = self;
    [self.followingDelegate fetchDataOfPage:1];
    
    //follower table view
    [self.followerDelegate bindWithTableView:self.followerTableView];
    self.followerDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollower:weakSelf.peopleDict page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.followerDelegate.delegate = self;
    [self.followerDelegate fetchDataOfPage:1];
    
#warning 需要换成正确的api
    //Show collectioin view
    [self.showsDelegate bindWithCollectionView:self.showCollectionView];
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
    [self updateView];


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
    [SHARE_NW_ENGINE handleFollowModel:self.peopleDict onSucceed:^(BOOL f) {
        [self updateView];
        if (f) {
            [self showTextHud:@"follow successfully"];
        } else {
            [self showTextHud:@"unfollow successfully"];
        }
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}

- (void)updateView
{
    [self.badgeView bindWithPeopleDict:self.peopleDict];
    [self.badgeView.btnGroup setNumber:[QSPeopleUtil getNumberShowsDescription:self.peopleDict] atIndex:0];
    [self.badgeView.btnGroup setNumber:[QSPeopleUtil getNumberFollowingsDescription:self.peopleDict] atIndex:1];
    [self.badgeView.btnGroup setNumber:[QSPeopleUtil getNumberFollowersDescription:self.peopleDict] atIndex:2];
}

@end
