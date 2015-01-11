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
#import "QSBrandUtil.h"
#import "UIViewController+QSExtension.h"
#import "QSItemImageListTableViewDelegateObj.h"
#import "QSS03ShowDetailViewController.h"

@interface QSP03BrandDetailViewController ()

@property (strong, nonatomic) NSDictionary* brandDict;
@property (strong, nonatomic) NSDictionary* itemDict;

#pragma mark Delegate Obj
@property (strong, nonatomic) QSItemImageListTableViewDelegateObj* itemNewDelegate;
@property (strong, nonatomic) QSItemImageListTableViewDelegateObj* itemDiscountDelegate;
@property (strong, nonatomic) QSBigImageTableViewDelegateObj* showsDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followerDelegate;

@end

@implementation QSP03BrandDetailViewController
#pragma mark - Init Method
- (id)initWithBrand:(NSDictionary*)brandDict
{
    self = [self initWithBrand:brandDict item:nil];
    if (self) {
        
    }
    return self;
}
- (id)initWithBrand:(NSDictionary *)brandDict item:(NSDictionary*)itemDict
{
    self = [super init];
    if (self) {
        self.brandDict = brandDict;
        self.itemDict = itemDict;
        
        self.type = QSSectionButtonGroupTypeBrand;
        
        [self delegateObjInit];
    }
    return self;
}

- (void)delegateObjInit
{
    self.itemNewDelegate = [[QSItemImageListTableViewDelegateObj alloc] init];
    self.itemDiscountDelegate = [[QSItemImageListTableViewDelegateObj alloc] init];
    self.showsDelegate = [[QSBigImageTableViewDelegateObj alloc] init];
    self.followerDelegate = [[QSModelListTableViewDelegateObj alloc] init];
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
    [self.itemNewDelegate refreshClickedData];
    [self.itemDiscountDelegate refreshClickedData];
    [self.showsDelegate refreshClickedData];
    [self.followerDelegate refreshClickedData];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -
- (void)configView
{
    //title
    self.title = [QSBrandUtil getBrandName:self.brandDict];
    [self.badgeView bindWithBrandDict:self.brandDict];
    
    //Show and Hide
    self.viewArray = @[self.itemNewTableView,
                       self.itemDiscountTableView,
                       self.showTableView,
                       self.followerTableView];

    self.itemNewTableView.hidden = NO;
    self.itemDiscountTableView.hidden = YES;
    self.showTableView.hidden = YES;
    self.followerTableView.hidden = YES;
    
    //Section title
    NSArray* titleArray = @[@"最新", @"优惠", @"秀", @"粉丝"];
    for (int i = 0; i < titleArray.count; i++) {
        [self.badgeView.btnGroup setNumber:@(0).stringValue atIndex:i];
        [self.badgeView.btnGroup setTitle:titleArray[i] atIndex:i];
    }
}
- (void)bindDelegateObj
{
    __weak QSP03BrandDetailViewController* weakSelf = self;
    //Item New
    [self.itemNewDelegate bindWithTableView:self.itemNewTableView];
    self.itemNewDelegate.type = QSItemImageListTableViewDelegateObjTypeNew;
    if (self.itemDict) {
        self.itemNewDelegate.additionalResult = @[self.itemDict];
    }
    self.itemNewDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getItemFeedingByBrandNew:weakSelf.brandDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
#warning 数字要不要加一
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:0];
            succeedBlock(array, metadata);
        } onError:errorBlock];
    };
    self.itemNewDelegate.delegate = self;
    [self.itemNewDelegate fetchDataOfPage:1];
    
    //Item Discount
    [self.itemDiscountDelegate bindWithTableView:self.itemDiscountTableView];
    self.itemDiscountDelegate.type = QSItemImageListTableViewDelegateObjTypeDiscount;
    self.itemDiscountDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getItemFeedingByBrandDiscount:weakSelf.brandDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:1];
            succeedBlock(array, metadata);
        } onError:errorBlock];
    };
    self.itemDiscountDelegate.delegate = self;
    [self.itemDiscountDelegate fetchDataOfPage:1];
    
    //Show collectioin view
    [self.showsDelegate bindWithTableView:self.showTableView];
    self.showsDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE feedingByBrand:weakSelf.brandDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:2];
            succeedBlock(array, metadata);
        } onError:errorBlock];
    };
    self.showsDelegate.delegate = self;
    [self.showsDelegate fetchDataOfPage:1];
    
    //follower table view
    [self.followerDelegate bindWithTableView:self.followerTableView];
    self.followerDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryBrandFollower:weakSelf.brandDict page:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
            [weakSelf.badgeView.btnGroup setNumber:[QSMetadataUtil getNumberTotalDesc:metadata] atIndex:3];
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
            [self showSuccessHudWithText:@"关注成功"];
        } else {
            [self showSuccessHudWithText:@"取消关注成功"];
        }
        [self.badgeView bindWithBrandDict:self.brandDict];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
- (void)clickModel:(NSDictionary*)model
{
    [self showPeopleDetailViewControl:model];
}
- (void)didClickCell:(UITableViewCell*)cell ofData:(NSDictionary*)dict
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:dict];
    [self.navigationController pushViewController:vc animated:YES];
}
@end
