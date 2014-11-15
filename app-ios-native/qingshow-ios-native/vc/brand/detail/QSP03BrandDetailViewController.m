//
//  QSP03BrandDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP03BrandDetailViewController.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"


@interface QSP03BrandDetailViewController ()

@property (strong, nonatomic) NSMutableDictionary* brandDict;

#pragma mark Delegate Obj
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* showsDelegate;
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* discountDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followerDelegate;

@end

@implementation QSP03BrandDetailViewController
#pragma mark - Init Method
- (id)initWithBrand:(NSDictionary*)brandDict
{
    self = [super init];
    if (self) {
        self. brandDict = [brandDict mutableCopy];
        
        [self delegateObjInit];
    }
    return self;
}

- (void)delegateObjInit
{
    self.showsDelegate = [[QSShowCollectionViewDelegateObj alloc] init];
    self.discountDelegate = [[QSShowCollectionViewDelegateObj alloc] init];
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

#pragma mark -
- (void)configView
{
    //title
    self.title = self.brandDict[@"name"];
    [self.badgeView bindWithBrandDict:self.brandDict];
    
    //Show and Hide
    self.viewArray = @[self.showCollectionView,
                       self.discountCollectionView,
                       self.followerTableView];
    
    self.showCollectionView.hidden = NO;
    self.discountCollectionView.hidden = YES;
    self.followerTableView.hidden = YES;
    
    //Section title
    NSArray* titleArray = @[@"搭配",@"优惠",@"粉丝"];
    for (int i = 0; i < 3; i++) {
        [self.badgeView.btnGroup setNumber:@(0).stringValue atIndex:i];
        [self.badgeView.btnGroup setTitle:titleArray[i] atIndex:i];
    }
}
- (void)bindDelegateObj
{
    //following table view
    [self.discountDelegate bindWithCollectionView:self.discountCollectionView];
    __weak QSP03BrandDetailViewController* weakSelf = self;
    
    self.discountDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getFeedByModel:weakSelf.brandDict[@"_id"] page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.discountDelegate.delegate = self;
    [self.discountDelegate fetchDataOfPage:1];
    
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
    self.showsDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getFeedByModel:weakSelf.brandDict[@"_id"] page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.showsDelegate.delegate = self;
    [self.showsDelegate fetchDataOfPage:1];

}

#pragma mark - 
- (void)singleButtonPressed
{
    NSNumber* hasFollowed = self.brandDict[@"hasFollowed"];
    if (hasFollowed && hasFollowed.boolValue) {
        [SHARE_NW_ENGINE unfollowPeople:self.brandDict[@"_id"] onSucceed:^{
            [self showTextHud:@"unfollow successfully"];
            self.brandDict[@"hasFollowed"] = @NO;
            [self.badgeView bindWithPeopleDict:self.brandDict];
        } onError:^(NSError *error) {
            [self showTextHud:@"error"];
        }];
    }
    else {
        [SHARE_NW_ENGINE followPeople:self.brandDict[@"_id"] onSucceed:^{
            [self showTextHud:@"follow successfully"];
            self.brandDict[@"hasFollowed"] = @YES;
            [self.badgeView bindWithPeopleDict:self.brandDict];
        } onError:^(NSError *error) {
            [self showTextHud:@"error"];
        }];
    }
}
- (void)clickModel:(NSDictionary*)model
{
    
}

@end
