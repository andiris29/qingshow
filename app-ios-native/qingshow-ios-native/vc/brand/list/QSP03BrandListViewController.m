//
//  QSBrandListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP03BrandListViewController.h"
#import "QSNetworkEngine.h"

@interface QSP03BrandListViewController ()

@property (strong, nonatomic) QSBrandCollectionViewDelegateObj* delegateObj;

@end

@implementation QSP03BrandListViewController

#pragma mark - Init Method
- (id)init
{
    self = [self initWithNibName:@"QSP03BrandListViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configDelegateObj];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - 
- (void)configDelegateObj
{
    self.delegateObj = [[QSBrandCollectionViewDelegateObj alloc] init];
    self.delegateObj.delegate = self;
    [self.delegateObj bindWithCollectionView:self.collectionView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.delegateObj fetchDataOfPage:1];
}
#pragma mark - QSBrandCollectionViewDelegateObjDelegate
- (void)didClickBrand:(NSDictionary*)brandDict {

}
@end

