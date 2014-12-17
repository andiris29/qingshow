//
//  QSS03ShopDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/16/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "QSS03ItemShopDetailViewController.h"
#import "QSAllItemImageScrollView.h"
#import "QSShowUtil.h"

@interface QSS03ItemShopDetailViewController ()

@property (strong, nonatomic) NSDictionary* showDict;
@property (assign, nonatomic) int currentItemIndex;
@property (strong, nonatomic) QSAllItemImageScrollView* imgScrollView;
@end

@implementation QSS03ItemShopDetailViewController

#pragma mark - Init
- (id)initWithShow:(NSDictionary*)showDict currentItemIndex:(int)index
{
    self = [super initWithNibName:@"QSS03ItemShopDetailViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
        self.currentItemIndex = index;
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
    self.showBtn.layer.cornerRadius = 8;
    self.showBtn.layer.masksToBounds = YES;
    self.imgScrollView = [[QSAllItemImageScrollView alloc] initWithFrame:self.contentView.bounds direction:QSImageScrollViewDirectionVer];
//    self.imgScrollView.backgroundColor = [UIColor blackColor];
    self.imgScrollView.pageControl.hidden = YES;
    [self.contentView addSubview:self.imgScrollView];
    
    
    self.imgScrollView.itemsArray = [QSShowUtil getItems:self.showDict];
}
- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    [self.imgScrollView scrollViewDidEndDecelerating:nil];
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
#pragma mark - IBAction
- (IBAction)shopButtonPressed:(id)sender {
    
}

@end
