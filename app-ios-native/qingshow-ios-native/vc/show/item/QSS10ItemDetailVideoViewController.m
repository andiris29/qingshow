//
//  QSS11ItemDetailVideoViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/17/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS10ItemDetailVideoViewController.h"
#import "QSS11CreateTradeViewController.h"
#import "QSItemUtil.h"
#import "QSImageNameUtil.h"
#import "UILabelStrikeThrough.h"
#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"
#import "QSUserManager.h"
#import "QSU07RegisterViewController.h"
#define PAGE_ID @"S10 - 商品详细"

@interface QSS10ItemDetailVideoViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;

@end

@implementation QSS10ItemDetailVideoViewController

#pragma mark - Init
- (instancetype)initWithItem:(NSDictionary*)itemDict
{
    self = [super initWithNibName:@"QSS10ItemDetailVideoViewController" bundle:nil];
    if (self) {
        self.itemDict = itemDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.imageScrollView.pageControlOffsetY = 10.f;
    [self bindWithDict:self.itemDict];
    self.priceLabel.hidden = YES;
   
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

#pragma mark - IBAction
- (IBAction)shopBtnPressed:(id)sender
{
    NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
    NSLog(@"dic = %@",peopleDic);
    if (!peopleDic) {
        UIViewController *vc = [[QSU07RegisterViewController alloc]init];
        [self.navigationController pushViewController:vc animated:YES];
    }
    else
    {
        UIViewController* vc = [[QSS11CreateTradeViewController alloc] initWithDict:self.itemDict];
        [self.navigationController pushViewController:vc animated:YES];
    }
  
}
#pragma mark - Private
- (void)bindWithDict:(NSDictionary*)itemDict
{
    [self updateShowImgScrollView];
    self.nameLabel.text = [QSItemUtil getItemName:itemDict];
    //Discount
    if ([QSItemUtil hasDiscountInfo:itemDict]) {
        self.priceLabel.hidden = NO;
        self.priceAfterDiscountLabel.text = [QSItemUtil getPriceAfterDiscount:itemDict];
        self.priceLabel.text = [QSItemUtil getPrice:itemDict];
        self.priceLabel.isWithStrikeThrough = YES;
        [self.priceAfterDiscountLabel sizeToFit];
        [self.priceLabel sizeToFit];
        
    } else {
        self.priceLabel.isWithStrikeThrough = NO;
        self.priceAfterDiscountLabel.hidden = YES;
        self.priceAfterDiscountLabel.text = @"";
        self.priceLabel.text = [QSItemUtil getPrice:itemDict];
        [self.priceLabel sizeToFit];
        [self.priceAfterDiscountLabel sizeToFit];
    }

    
    self.discountContainer.hidden = YES;
}


#pragma mark - Override
#pragma mark Data
- (NSArray*)generateImagesData
{
    return [QSItemUtil getImagesUrl:self.itemDict];
}
- (NSString*)generateVideoPath
{
    return [QSItemUtil getVideoPath:self.itemDict];
//    return @"http://trial01.focosee.com/demo6/1211a50300.mp4";
}

#pragma mark Btn
- (void)setPlayModeBtnsHidden:(BOOL)hidden
{

    self.backBtn.hidden = hidden;
    [self setBtnsHiddenExceptBackAndPlay:hidden];
    
}
- (void)setBtnsHiddenExceptBack:(BOOL)hidden
{
    [self setBtnsHiddenExceptBackAndPlay:hidden];

}
- (void)setBtnsHiddenExceptBackAndPlay:(BOOL)hidden
{
    self.btnPanel.hidden = hidden;
    self.shadow.hidden = hidden;
    self.labelContainer.hidden = hidden;
}

#pragma mark Mob
- (void)logMobPlayVideo:(NSTimeInterval)playbackTime
{
    [MobClick event:@"playVideo" attributes:@{@"showId" : self.itemDict[@"_id"], @"length": @(playbackTime).stringValue} durations:(int)(playbackTime * 1000)];
}
@end
