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
#import "UIViewController+ShowHud.h"
#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"
#import "QSTradeUtil.h"
#import "QSUserManager.h"
#import "QSU07RegisterViewController.h"
#import "QSPeopleUtil.h"
#import "QSU11ReceiverEditingViewController.h"
#define PAGE_ID @"G01 - 内嵌浏览器"

@interface QSG01ItemWebViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;

@property (strong, nonatomic) IBOutlet UIView *discountLayerContainer;
@property (weak, nonatomic) IBOutlet UIImageView *discountBackgroundView;
@property (weak, nonatomic) IBOutlet UIView *discountTableViewContainer;
@property (strong, nonatomic) QSDiscountTableViewController* discountVc;
@property (strong, nonatomic) MKNetworkOperation* createTradeOp;

@property (assign, nonatomic) BOOL hasSyncItem;

@property (strong, nonatomic) MKNetworkOperation* syncOp;
@property (strong, nonatomic) MBProgressHUD* hud;


@end

@implementation QSG01ItemWebViewController
#pragma mark - Init Method
- (id)initWithItem:(NSDictionary*)item 
{
    self = [super initWithNibName:@"QSG01ItemWebViewController" bundle:nil];
    if (self) {
        self.itemDict = item;
        self.discountVc = [[QSDiscountTableViewController alloc] initWithItem:item];
        self.hasSyncItem = NO;
    }
    return self;
}
- (id)initWithItemId:(NSString*)itemId
{
    if (self = [super initWithNibName:@"QSG01ItemWebViewController" bundle:nil]) {
        
    }
    return self;
}
- (id)initWithItem:(NSDictionary*)item showId:(NSString *)showId
{
    self = [super initWithNibName:@"QSG01ItemWebViewController" bundle:nil];
    if (self) {
        self.itemDict = item;
        self.discountVc = [[QSDiscountTableViewController alloc] initWithItem:item];
        self.discountVc.showId = showId;
        self.hasSyncItem = NO;
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

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    if ([UIScreen mainScreen].bounds.size.width == 320 && [UIScreen mainScreen].bounds.size.height == 480) {
        CGRect submitFrame = CGRectMake(165, 420, 115, 33);
        CGRect cancelFrame = CGRectMake(10, 420, 115, 33);
        self.submitBtn.frame = submitFrame;
        self.cancelBtn.frame = cancelFrame;
    }
    if ([UIScreen mainScreen].bounds.size.width == 414) {
        self.view.transform = CGAffineTransformMakeScale(1.35, 1.35);
    }
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    self.discountBtn.hidden = [QSItemUtil getReadOnly:self.itemDict];
    NSURL* url = [QSItemUtil getShopUrl:self.itemDict];
    [self.webView loadRequest:[NSURLRequest requestWithURL:url]];
    [self.webView setScalesPageToFit:YES];
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

    
    self.submitBtn.layer.cornerRadius = 5.f;
    self.submitBtn.layer.masksToBounds = YES;
    self.cancelBtn.layer.cornerRadius = 5.f;
    self.cancelBtn.layer.masksToBounds = YES;
    
    
    [SHARE_NW_ENGINE itemSync:[QSEntityUtil getIdOrEmptyStr:self.itemDict] onSucceed:^(NSDictionary *data, NSDictionary *metadata) {
        
        self.hasSyncItem = YES;
        self.itemDict = data;
        self.discountVc.itemDict = self.itemDict;
        [self.discountVc refresh];
        
        if (self.hud) {
            [self.hud hide:YES];
            self.hud = nil;
            self.discountLayerContainer.hidden = NO;
        }
        
    } onError:^(NSError *error) {
        if (self.hud) {
            [self.hud hide:YES];
            self.hud = nil;
            [self showErrorHudWithText:@"活动结束"];
        }
        self.discountBtn.hidden = YES;
    }];
    if (self.isDisCountBtnHidden == YES) {
        self.discountBtn.hidden = YES;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -
- (IBAction)backBtnPressed:(id)sender {
    self.navigationController.navigationBarHidden = NO;
    [self.navigationController popViewControllerAnimated:YES];
}
- (IBAction)discountBtnPressed:(id)sender {
    if (self.hasSyncItem) {
        self.discountLayerContainer.hidden = NO;
    } else {
        if (!self.hud) {
            self.hud = [self showNetworkWaitingHud];
        }
    }

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
- (IBAction)submitBtnPressed:(id)sender {
    if (![self.discountVc checkComplete]) {
        [self showErrorHudWithText:@"信息不完整"];
    } else {
        NSDictionary *people = [QSUserManager shareUserManager].userInfo;
        if (people && ([QSPeopleUtil checkMobileExist:people] == NO)) {
            UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"请填写收货信息" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
            [alert show];
        }else{
            self.createTradeOp =
                [SHARE_NW_ENGINE createOrderArray:@[[self.discountVc getResult]] onSucceed:^(NSDictionary *dict) {
            [self showSuccessHudAndPop:@"创建成功"];
            self.createTradeOp = nil;
        } onError:^(NSError *error) {
            [self handleError:error];
            self.createTradeOp = nil;
        }];
            //[self delisthandle];
        }
    }
}
- (void)delisthandle
{
    NSDictionary *newTradeDic = [self.discountVc getResult];
    NSDictionary *itemDic = [QSTradeUtil getItemSnapshot:newTradeDic];
    NSString *itemId = [QSItemUtil getItemId:itemDic];
    NSArray *skuArray = newTradeDic[@"selectedSkuProperties"];
    NSString *key = [QSItemUtil getKeyValueForSkuTableFromeSkuProperties:skuArray];
    [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSArray *array, NSDictionary *metadata) {
        NSDictionary *dic = [array firstObject];
        int count = [QSItemUtil getFirstValueFromSkuTableWithkey:key itemDic:dic];
        if (count < 1) {
            UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"您来晚啦，这个规格的已经卖完啦！" delegate:self cancelButtonTitle:@"确认" otherButtonTitles:nil,nil];
            [alert show];
        }
        else{
            self.createTradeOp =
            [SHARE_NW_ENGINE createOrderArray:@[[self.discountVc getResult]] onSucceed:^(NSDictionary *dict) {
                [self showSuccessHudAndPop:@"创建成功"];
                self.createTradeOp = nil;
            } onError:^(NSError *error) {
                [self handleError:error];
                self.createTradeOp = nil;
            }];
        }
    } onError:^(NSError *error) {
        
    }];
}
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1) {
        self.navigationController.navigationBarHidden = NO;
        UIViewController* vc = [[QSU11ReceiverEditingViewController alloc] initWithDict:nil];
        UIBarButtonItem *backItem = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"nav_btn_back"] style:UIBarButtonItemStyleDone target:self action:@selector(backAction)];
        vc.navigationItem.leftBarButtonItem = backItem;
        [self.navigationController pushViewController:vc animated:YES];

    }
}
- (void)backAction
{
    [self.navigationController popViewControllerAnimated:YES];
}
- (IBAction)cancelBtnPressed:(id)sender {
    [self closeBtnPressed:nil];
    
}

@end
