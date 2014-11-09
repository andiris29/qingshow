//
//  QSModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP02ModelDetailViewController.h"
#import "QSModelBadgeView.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"

@interface QSP02ModelDetailViewController ()

@property (strong, nonatomic) QSModelBadgeView* badgeView;
@property (strong, nonatomic) NSArray* viewArray;
#pragma mark - Data
@property (strong, nonatomic) NSMutableDictionary* peopleDict;
@property (assign, nonatomic) int currentSection;
@property (strong, nonatomic) NSMutableArray* showsArray;
@property (strong, nonatomic) NSMutableArray* followingArray;
@property (strong, nonatomic) NSMutableArray* followerArray;

@property (assign, nonatomic) CGPoint touchLocation;
@property (strong, nonatomic) UIView* currentTouchView;



#pragma mark - Delegate Obj
@property (strong, nonatomic) QSShowWaterfallDelegateObj* showsDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followingDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followerDelegate;

@end

@implementation QSP02ModelDetailViewController

#pragma mark - Init
- (id)initWithModel:(NSDictionary*)peopleDict
{
    self = [self initWithNibName:@"QSP02ModelDetailViewController" bundle:nil];
    if (self)
    {
        self.peopleDict = [peopleDict mutableCopy];
        
        [self delegateObjInit];
    }
    return self;
}
- (void)delegateObjInit
{
    self.showsDelegate = [[QSShowWaterfallDelegateObj alloc] init];
    self.followingDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    self.followerDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    
    
}

#pragma mark - View
- (void)configView
{
    //title
    self.title = self.peopleDict[@"name"];
    //badge view
    self.badgeView = [QSModelBadgeView generateView];
    [self.badgeContainer addSubview:self.badgeView];
    self.badgeView.delegate = self;
    [self.badgeView bindWithDict:self.peopleDict];
    
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
    
    //Show collectioin view
    [self.showsDelegate bindWithCollectionView:self.showCollectionView];
    __weak QSP02ModelDetailViewController* weakSelf = self;
    
    self.showsDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getFeedByModel:weakSelf.peopleDict[@"_id"] page:page onSucceed:succeedBlock onError:errorBlock];
//        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.showsDelegate.delegate = self;
    [self.showsDelegate fetchDataOfPage:1];
}


#pragma mark - Network



#pragma mark - Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self configView];
    self.viewArray = @[self.showCollectionView, self.followingTableView, self.followerTableView];
    self.currentSection = 0;
    self.showCollectionView.hidden = NO;
    self.followerTableView.hidden = YES;
    self.followingTableView.hidden = YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - QSModelBadgeViewDelegate
- (void)changeToSection:(int)section
{
    UIScrollView* currentView = self.viewArray[self.currentSection];
    CGPoint p = currentView.contentOffset;
    
    for (int i = 0; i < self.viewArray.count; i++) {
        UIScrollView* view = self.viewArray[i];
        view.hidden = i != section;
        if (p.y < 0.1) {
            view.contentOffset = p;
        }
    }
    
    self.currentSection = section;
}
- (void)followButtonPressed
{
    NSNumber* hasFollowed = self.peopleDict[@"hasFollowed"];
    if (hasFollowed && hasFollowed.boolValue) {
        [SHARE_NW_ENGINE unfollowPeople:self.peopleDict[@"_id"] onSucceed:^{
            [self showTextHud:@"unfollow successfully"];
            self.peopleDict[@"hasFollowed"] = @NO;
            [self.badgeView bindWithDict:self.peopleDict];
        } onError:^(NSError *error) {
            [self showTextHud:@"error"];
        }];
    }
    else {
        [SHARE_NW_ENGINE followPeople:self.peopleDict[@"_id"] onSucceed:^{
            [self showTextHud:@"follow successfully"];
            self.peopleDict[@"hasFollowed"] = @YES;
            [self.badgeView bindWithDict:self.peopleDict];
        } onError:^(NSError *error) {
            [self showTextHud:@"error"];
        }];
    }
    
}

#pragma mark - Scroll View
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.currentTouchView != scrollView) {
        self.currentTouchView = scrollView;
        self.touchLocation = scrollView.contentOffset;
    }
    else
    {
        self.topConstrain.constant -= scrollView.contentOffset.y;
        BOOL f = YES;
        if (self.topConstrain.constant > 0) {
            self.topConstrain.constant = 0;
            f = NO;
        }
        if (self.topConstrain.constant < -115) {
            self.topConstrain.constant = -115;
            f = NO;
        }
        if (f) {
            scrollView.contentOffset = CGPointZero;
        }

        [self.view layoutIfNeeded];
        
    }
}

- (void)clickModel:(NSDictionary*)model
{

}
- (void)addFavorModel:(NSDictionary*)model
{

}

@end
