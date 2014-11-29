//
//  QSS02CategoryViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS02CategoryViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSP02ModelDetailViewController.h"
#import "UIViewController+ShowHud.h"
#import "QSNetworkEngine.h"

@interface QSS02CategoryViewController ()
@property (assign, nonatomic) QSFeedingCategory type;
@property (strong, nonatomic) QSShowCollectionViewDelegateObj* delegateObj;
@end

@implementation QSS02CategoryViewController
#pragma mark - Init
- (id)initWithCategory:(QSFeedingCategory)type;
{
    self = [super initWithNibName:@"QSS02CategoryViewController" bundle:nil];
    if (self) {
        self.type = type;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configDelegateObj];
    self.title = categoryToString(self.type);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - 
- (void)configDelegateObj
{
    self.delegateObj = [[QSShowCollectionViewDelegateObj alloc] init];
    self.delegateObj.delegate = self;
    [self.delegateObj bindWithCollectionView:self.collectionView];
    __weak QSS02CategoryViewController* weakSelf = self;
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getCategoryFeeding:weakSelf.type page:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.delegateObj fetchDataOfPage:1];
}
#pragma mark - QSShowDelegateObjDelegate
#pragma mark -
- (void)didClickShow:(NSDictionary*)showDict
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPeople:(NSDictionary *)peopleDict
{
    UIViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)handleNetworkError:(NSError*)error
{
    [self showErrorHudWithError:error];
}
@end
