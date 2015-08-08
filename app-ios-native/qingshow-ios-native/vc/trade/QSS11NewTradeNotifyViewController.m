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

#define PAGE_ID @"推荐折扣"

@interface QSS11NewTradeNotifyViewController ()

@property (strong, nonatomic) QS11OrderInfoCell* orderInfoCell;
@property (strong, nonatomic) QS11TextCell* textCell;
@property (strong, nonatomic) NSDictionary* tradeDict;
@end

@implementation QSS11NewTradeNotifyViewController

- (instancetype)initWIthDict:(NSDictionary*)tradeDict {
    self = [super initWithNibName:@"QSS11NewTradeNotifyViewController" bundle:nil];
    if (self) {
        self.tradeDict = tradeDict;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIImage* img = [UIImage imageNamed:@"discount_container_bg"];
    img = [img resizableImageWithCapInsets:UIEdgeInsetsMake(20, 20, 20, 20)];
    self.backgroundImgView.image = img;
    self.orderInfoCell = [QS11OrderInfoCell generateView];
    self.textCell = [QS11TextCell generateView];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)closeNotifyViewController:(id)sender {
//    QSS01MatchShowsViewController *vc = [[QSS01MatchShowsViewController alloc]init];
//    [self.navigationController pushViewController:vc animated:YES];
    
}
- (IBAction)shareAndBuyBtnPressed:(id)sender {
    if ([self.delelgate respondsToSelector:@selector(didClickClose:)]) {
        [self.delelgate didClickClose:self];
    }
}

#pragma mark - Table View
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        [self.orderInfoCell bindWithDict:self.tradeDict];
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
