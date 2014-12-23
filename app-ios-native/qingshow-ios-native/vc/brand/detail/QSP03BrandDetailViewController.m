//
//  QSP03BrandDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP03BrandDetailViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSMetadataUtil.h"
#import "UIViewController+Network.h"

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
    __weak QSP03BrandDetailViewController* weakSelf = self;
    
    //Show collectioin view
    [self.showsDelegate bindWithCollectionView:self.showCollectionView];
    self.showsDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE feedingByBrand:weakSelf.brandDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:0];
            succeedBlock(array, metadata);
        } onError:errorBlock];
    };
    self.showsDelegate.delegate = self;
    [self.showsDelegate fetchDataOfPage:1];
    

    //following table view
    [self.discountDelegate bindWithCollectionView:self.discountCollectionView];
    self.discountDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE feedingByBrandDiscount:weakSelf.brandDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:1];
            succeedBlock(array, metadata);
        } onError:errorBlock];
    };
    self.discountDelegate.delegate = self;
    [self.discountDelegate fetchDataOfPage:1];
    
    //follower table view
    [self.followerDelegate bindWithTableView:self.followerTableView];
    self.followerDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryBrandFollower:weakSelf.brandDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:2];
            succeedBlock(array, metadata);
        } onError:errorBlock];
    };
    self.followerDelegate.delegate = self;
    [self.followerDelegate fetchDataOfPage:1];
}

#pragma mark - 
- (void)singleButtonPressed
{
    [SHARE_NW_ENGINE handleFollowBrand:self.brandDict onSucceed:^(BOOL f) {
        if (f) {
            [self showSuccessHudWithText:@"follow successfully"];
        } else {
            [self showSuccessHudWithText:@"unfollow successfully"];
        }
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
- (void)clickModel:(NSDictionary*)model
{
    
}

@end
