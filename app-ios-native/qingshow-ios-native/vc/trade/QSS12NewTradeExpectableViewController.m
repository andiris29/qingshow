//
//  QSS11NewTradeNotifyViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS12NewTradeExpectableViewController.h"
#import "QSS12TradeInfoCell.h"

#import "QSNotificationHelper.h"
#import "QSTableViewBasicProvider.h"
#import "QSTradeUtil.h"
#import "QSNetworkKit.h"
#import "QSItemUtil.h"
#define PAGE_ID @"推荐折扣"
#define w ([UIScreen mainScreen].bounds.size.width-50)
#define h ([UIScreen mainScreen].bounds.size.height)
@interface QSS12NewTradeExpectableViewController ()

@property (strong, nonatomic) QSS12TradeInfoCell* orderInfoCell;
@property (strong, nonatomic) QSS12TradeDiscountCell* textCell;


@end


@implementation QSS12NewTradeExpectableViewController

#pragma mark - Init
- (instancetype)initWithDict:(NSDictionary*)tradeDict {
    self = [super initWithNibName:@"QSS12NewTradeExpectableViewController" bundle:nil];
    if (self) {
        self.tradeDict = tradeDict;
#warning TODO Remove This Controller?
//        self.expectablePrice = [QSItemUtil getExpectablePrice:[QSTradeUtil getItemDic:tradeDict]];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIImage* img = [UIImage imageNamed:@"discount_container_bg"];
    img = [img resizableImageWithCapInsets:UIEdgeInsetsMake(20, 20, 20, 20)];
    self.backgroundImgView.image = img;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.orderInfoCell = [QSS12TradeInfoCell generateView];
    self.textCell = [QSS12TradeDiscountCell generateView];
    self.textCell.delegate = self;
}

- (void)viewDidLayoutSubviews
{
    CGRect frame = self.titleLabel.frame;
    frame.origin.x = [UIScreen mainScreen].bounds.size.width/2-40;
    self.titleLabel.frame = frame;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)closeNotifyViewController:(id)sender {
    [QSNotificationHelper postHideTradeExpectablePriceChangeVcNoti];
}

#pragma mark - Table View
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 2;
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        [self.orderInfoCell bindWithDict:self.tradeDict ];
        self.orderInfoCell.selectionStyle = UITableViewCellSelectionStyleNone;
        self.orderInfoCell.transform = CGAffineTransformMakeScale(w/270, w/270);
        return self.orderInfoCell;
    } else {
        
        [self.textCell bindWithDict:self.tradeDict actualPrice:self.expectablePrice];
        self.textCell.selectionStyle = UITableViewCellSelectionStyleNone;
        self.textCell.transform = CGAffineTransformMakeScale(w/270, w/270);
        return self.textCell;
    }
    
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        return [QSS12TradeInfoCell cellHeight] * (w/270);
    } else {
        return [QSS12TradeDiscountCell cellHeight] * (w/270);
    }
}
- (void)didClickShareToPayOfCell:(UITableViewCell*)cell {
    if ([self.delelgate respondsToSelector:@selector(didClickPay:)]) {
        [self.delelgate didClickPay:self];
    }
}
@end
