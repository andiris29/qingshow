//
//  QSS03ShopDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/16/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "QSS03ItemShopDetailViewController.h"

#import "QSShowUtil.h"
#import "QSItemUtil.h"
#import "QSBrandUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "UIViewController+ShowHud.h"

#define PAGE_ID @"S03 - 秀"

@interface QSS03ItemShopDetailViewController ()

@property (strong, nonatomic) NSDictionary* showDict;
@property (assign, nonatomic) int currentItemIndex;
@property (strong, nonatomic) QSAllItemImageScrollView* imgScrollView;

@property (assign, nonatomic) BOOL fIsFirstLoad;
@end

@implementation QSS03ItemShopDetailViewController

#pragma mark - Init
- (id)initWithShow:(NSDictionary*)showDict currentItemIndex:(int)index
{
    self = [super initWithNibName:@"QSS03ItemShopDetailViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
        self.currentItemIndex = index;
        self.fIsFirstLoad = YES;
    }
    return self;
}

- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.iconImageView.layer.cornerRadius = self.iconImageView.frame.size.height / 2;
    self.iconImageView.layer.masksToBounds = YES;
    self.shopBtn.layer.cornerRadius = 8;
    self.shopBtn.layer.masksToBounds = YES;
    self.contentView.frame = [UIScreen mainScreen].bounds;
    self.imgScrollView = [[QSAllItemImageScrollView alloc] initWithFrame:self.contentView.bounds direction:QSImageScrollViewDirectionVer];
    self.imgScrollView.delegate = self;
//    self.imgScrollView.backgroundColor = [UIColor blackColor];
    [self.contentView addSubview:self.imgScrollView];
    
    
    self.imgScrollView.itemsArray = [QSShowUtil getItems:self.showDict];
    self.label2.isWithStrikeThrough = YES;

}
- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    if (self.fIsFirstLoad) {
        self.fIsFirstLoad = NO;
        [self.imgScrollView scrollToPage:self.currentItemIndex];
        NSDictionary* item = [QSShowUtil getItemFromShow:self.showDict AtIndex:self.currentItemIndex];
        [self bindWithItem:item];
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
- (IBAction)shopButtonPressed:(id)sender {
    NSURL* url = [QSItemUtil getShopUrl:[QSShowUtil getItemFromShow:self.showDict AtIndex:self.currentItemIndex]];
    if (url) {
        [[UIApplication sharedApplication] openURL:url];
    } else {
        [self showErrorHudWithText:@"暂无地址"];
    }
}


- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page
{
    if (!self.fIsFirstLoad) {
        self.currentItemIndex = page;
        NSDictionary* item = [QSShowUtil getItemFromShow:self.showDict AtIndex:page];
        [self bindWithItem:item];
    }
}

- (void)bindWithItem:(NSDictionary*)item
{
    self.label1.text = [QSItemUtil getItemName:item];
    self.label2.text = [QSItemUtil getPrice:item];
    self.label3.text = [QSItemUtil getPriceAfterDiscount:item];
    [self.label2 sizeToFit];
    [self.label3 sizeToFit];
    CGRect rect3 = self.label3.frame;
    rect3.origin.x = self.label2.frame.origin.x + self.label2.frame.size.width + 20.f;
    self.label3.frame = rect3;
    [self.label2 setNeedsDisplay];
    
    NSDictionary* brand = [QSItemUtil getBrand:item];
    NSURL* iconUrl = [QSBrandUtil getBrandLogoUrl:brand];
    if (iconUrl) {
        self.iconImageView.hidden = NO;
//        [self.iconImageView setImageFromURL:[QSItemUtil getIconUrl:item]];
    } else {
        self.iconImageView.hidden = YES;
    }

}
@end
