//
//  QSS11NewTradeNotifyViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS11NewTradeNotifyViewController.h"
#import "QS11OrderInfoCell.h"
#import "QS11TextCell.h"
#import "QSS01MatchShowsViewController.h"
#import "QSTableViewBasicProvider.h"
#import "QSTradeUtil.h"

#define PAGE_ID @"推荐折扣"

@interface QSS11NewTradeNotifyViewController ()

@property (strong, nonatomic) QS11OrderInfoCell* orderInfoCell;
@property (strong, nonatomic) QS11TextCell* textCell;


@end

@implementation QSS11NewTradeNotifyViewController

#pragma mark - Init
- (instancetype)initWithDict:(NSDictionary*)tradeDict actualPrice:(NSNumber*)actualPrice {
    self = [super initWithNibName:@"QSS11NewTradeNotifyViewController" bundle:nil];
    if (self) {
        self.tradeDict = tradeDict;
        self.actualPrice = actualPrice;
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
    self.orderInfoCell = [QS11OrderInfoCell generateView];
    self.textCell = [QS11TextCell generateView];
    
    if ([QSTradeUtil getShouldShare:self.tradeDict]) {
        [self.payBtn setTitle:@"分享后购买" forState:UIControlStateNormal];
    } else {
        [self.payBtn setTitle:@"购买" forState:UIControlStateNormal];
    }
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
    if ([self.delelgate respondsToSelector:@selector(didClickClose:)]) {
        [self.delelgate didClickClose:self];
    }
}
- (IBAction)shareAndBuyBtnPressed:(id)sender {
    if ([self.delelgate respondsToSelector:@selector(didClickPay:)]) {
        [self.delelgate didClickPay:self];
    }
}

#pragma mark - Table View
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        [self.orderInfoCell bindWithDict:self.tradeDict actualPrice:self.actualPrice];
        self.orderInfoCell.selectionStyle = UITableViewCellSelectionStyleNone;
        return self.orderInfoCell;
    } else {
        [self.textCell bindWithDict:self.tradeDict];
        self.textCell.selectionStyle = UITableViewCellSelectionStyleNone;
        return self.textCell;
    }
    
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        return 213;
    } else {
        return 152;
    }
}
@end
