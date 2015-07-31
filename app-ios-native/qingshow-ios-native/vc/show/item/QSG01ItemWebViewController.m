//
//  QSG01ItemWebViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/12/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSG01ItemWebViewController.h"
#import "QSItemUtil.h"
#import "QSEntityUtil.h"
#import "QSDiscountTableViewController.h"

#define PAGE_ID @"G01 - 内嵌浏览器"

@interface QSG01ItemWebViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;



@property (strong, nonatomic) IBOutlet UIView *discountLayerContainer;
@property (weak, nonatomic) IBOutlet UIImageView *discountBackgroundView;
@property (weak, nonatomic) IBOutlet UIView *discountTableViewContainer;
@property (strong, nonatomic) QSDiscountTableViewController* discountVc;

@end

@implementation QSG01ItemWebViewController
#pragma mark - Init Method
- (id)initWithItem:(NSDictionary*)item
{
    self = [super initWithNibName:@"QSG01ItemWebViewController" bundle:nil];
    if (self) {
        self.itemDict = item;
        self.discountVc = [[QSDiscountTableViewController alloc] initWithItem:item];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    NSURL* url = [QSItemUtil getShopUrl:self.itemDict];
    [self.webView loadRequest:[NSURLRequest requestWithURL:url]];
    
    [MobClick event:@"viewItemSource" attributes:@{@"itemId": [QSEntityUtil getIdOrEmptyStr:self.itemDict]} counter:1];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    
    [self.view addSubview:self.discountLayerContainer];
    self.discountLayerContainer.frame = CGRectMake(10, 20, self.discountLayerContainer.bounds.size.width - 20, self.discountLayerContainer.bounds.size.height - 40);
    self.discountLayerContainer.hidden = YES;
    UIImage* img = [UIImage imageNamed:@"discount_container_bg"];
    img = [img resizableImageWithCapInsets:UIEdgeInsetsMake(20, 20, 20, 20)];
    self.discountBackgroundView.image = img;
    
    self.discountVc.view.frame = self.discountTableViewContainer.bounds;
    [self.discountTableViewContainer addSubview:self.discountVc.view];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -
- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
- (IBAction)discountBtnPressed:(id)sender {
    self.discountLayerContainer.hidden = NO;
}
- (IBAction)closeBtnPressed:(id)sender {
    self.discountLayerContainer.hidden = YES;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
