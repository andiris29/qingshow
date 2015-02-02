//
//  QSP02ModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP02ModelDetailViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSPeopleUtil.h"
#import "QSMetadataUtil.h"
#import "UIViewController+QSExtension.h"

#define PAGE_ID @"P02"

@interface QSP02ModelDetailViewController ()

#pragma mark - Data
@property (strong, nonatomic) NSMutableDictionary* peopleDict;

#pragma mark - Delegate Obj
@property (strong, nonatomic) QSBigImageTableViewProvider* showsDelegate;
@property (strong, nonatomic) QSModelListTableViewProvider* followingDelegate;
@property (strong, nonatomic) QSModelListTableViewProvider* followerDelegate;


@end

@implementation QSP02ModelDetailViewController

#pragma mark - Init
- (id)initWithModel:(NSDictionary*)peopleDict
{
    self = [self initWithNibName:@"QSP02ModelDetailViewController" bundle:nil];
    if (self)
    {
        self.peopleDict = peopleDict;
        
        [self delegateObjInit];
    }
    return self;
}

- (void)delegateObjInit
{
    self.showsDelegate = [[QSBigImageTableViewProvider alloc] init];
    self.showsDelegate.delegate = self;
    self.showsDelegate.hasRefreshControl = NO;
    self.followingDelegate = [[QSModelListTableViewProvider alloc] init];
    self.followingDelegate.delegate = self;
    self.followingDelegate.hasRefreshControl = NO;
    self.followerDelegate = [[QSModelListTableViewProvider alloc] init];
    self.followerDelegate.delegate = self;
    self.followerDelegate.type = QSModelListTableViewDelegateObjTypeHideFollow;
    self.followerDelegate.hasRefreshControl = NO;
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
    [self.showsDelegate refreshClickedData];
    [self.followingDelegate refreshClickedData];
    [self.followerDelegate refreshClickedData];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
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
        return [SHARE_NW_ENGINE peopleQueryFollowed:weakSelf.peopleDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:1];
            succeedBlock(array, metadata);
        } onError:^(NSError* e){
            if (page == 1) {
                [weakSelf.badgeView.btnGroup setNumber:@"0" atIndex:1];
            }
            errorBlock(e);
        }];
    };
    self.followingDelegate.delegate = self;
    [self.followingDelegate fetchDataOfPage:1];
    
    //follower table view
    [self.followerDelegate bindWithTableView:self.followerTableView];
    self.followerDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE peopleQueryFollower:weakSelf.peopleDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:2];
            succeedBlock(array, metadata);
        } onError:^(NSError* e){
            if (page == 1) {
                [weakSelf.badgeView.btnGroup setNumber:@"0" atIndex:2];
            }
            errorBlock(e);
        }];
    };
    self.followerDelegate.delegate = self;
    [self.followerDelegate fetchDataOfPage:1];
    
    //Show table view
    self.showsDelegate.type = QSBigImageTableViewCellTypeModelEmpty;
    [self.showsDelegate bindWithTableView:self.showTableView];
    self.showsDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getFeedByModel:weakSelf.peopleDict[@"_id"] page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:0];
            succeedBlock(array, metadata);
        } onError:^(NSError* e){
            if (page == 1) {
                [weakSelf.badgeView.btnGroup setNumber:@"0" atIndex:0];
            }
            errorBlock(e);
        }];
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
    self.viewArray = @[self.showTableView, self.followingTableView, self.followerTableView];
    
    self.showTableView.hidden = NO;
    self.followerTableView.hidden = YES;
    self.followingTableView.hidden = YES;
    
    //Section title
    NSArray* titleArray = @[@"搭配",@"关注",@"粉丝"];
    for (int i = 0; i < 3; i++) {
        [self.badgeView.btnGroup setNumber:@(0).stringValue atIndex:i];
        [self.badgeView.btnGroup setTitle:titleArray[i] atIndex:i];
    }
    QSSectionFollowButton* followBtn = (QSSectionFollowButton*)self.badgeView.btnGroup.singleButton;
    [followBtn setFollowed:[QSPeopleUtil getPeopleIsFollowed:self.peopleDict]];
}


- (void)singleButtonPressed
{
    [SHARE_NW_ENGINE handleFollowModel:self.peopleDict onSucceed:^(BOOL f) {
        [self updateView];
        if (f) {
            [self showTextHud:@"关注成功"];
        } else {
            [self showTextHud:@"取消关注成功"];
        }
        [self.followerDelegate fetchDataOfPage:1];
    } onError:^(NSError *error) {
        [self updateView];
        [self handleError:error];
    }];
}

- (void)updateView
{
    [self.badgeView bindWithPeopleDict:self.peopleDict];
}
- (void)didClickCell:(UITableViewCell*)cell ofData:(NSDictionary*)dict type:(QSBigImageTableViewCellType)type
{
//    [self didClickShow:dict];
}
- (void)clickDetailOfDict:(NSDictionary *)dict type:(QSBigImageTableViewCellType)type
{
    [self didClickShow:dict];
}

@end
