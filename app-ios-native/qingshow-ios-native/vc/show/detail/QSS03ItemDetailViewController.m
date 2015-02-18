//
//  QSS03ItemDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ItemDetailViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSItemUtil.h"
#import "QSBrandUtil.h"

#define PAGE_ID @"S03"

@interface QSS03ItemDetailViewController ()

@property (strong, nonatomic) NSArray* items;
@property (strong, nonatomic) QSSingleImageScrollView* imageScrollView;
@property (strong, nonatomic) NSDictionary* currentItem;
@end

@implementation QSS03ItemDetailViewController

- (id)initWithItems:(NSArray*)items;
{
    self = [self initWithNibName:@"QSS03ItemDetailViewController" bundle:nil];
    if (self) {
        self.items = items;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.imageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:self.imageContainer.bounds];
    [self.imageContainer addSubview:self.imageScrollView];
    self.imageScrollView.frame = self.imageContainer.bounds;
    self.imageScrollView.delegate = self;
    self.imageScrollView.pageControl.hidden = YES;
    NSArray* urlArray = [QSItemUtil getItemsImageUrlArray:self.items];
    self.imageScrollView.imageUrlArray = urlArray;
    if (self.items.count) {
        [self bindItem:self.items[0]];
    }
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
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

#pragma mark - IBAction
- (IBAction)closeBtnPressed:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)brandBtnPressed:(id)sender {
    
}
- (IBAction)shopBtnPressed:(id)sender {
    NSURL* showUrl = [QSItemUtil getShopUrl:self.currentItem];

    if (showUrl) {
        [[UIApplication sharedApplication] openURL:showUrl];
    } else {
        [self showErrorHudWithText:@"暂无地址"];
    }

}
- (IBAction)didClickImage:(id)sender {
    [self closeBtnPressed:nil];
}

- (void)bindItem:(NSDictionary*)item
{
    self.desLabel.text = [QSItemUtil getItemName:item];
    NSDictionary* brandDict = [QSItemUtil getBrand:item];
    [self.brandBtn setTitle:[QSBrandUtil getBrandName:brandDict] forState:UIControlStateNormal];
}
#pragma mark - QSImageScrollViewBaseDelegate
- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page
{
    self.currentItem = self.items[page];
    [self bindItem:self.currentItem];
}
@end
