//
//  QSU15BonusViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU15BonusViewController.h"
#import "QSU16BonusListViewController.h"
#import "MKNetworkKit.h"
#import "QSPeopleUtil.h"
#import "QSShareService.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "QSUnreadManager.h"
#import "QSNetworkEngine+ShareService.h"
#import "QSShareUtil.h"
@interface QSU15BonusViewController ()

@property (strong, nonatomic) NSArray* bonusArray;
@property (assign, nonatomic) float currMoney;
@end

@implementation QSU15BonusViewController

#pragma mark - Init
- (instancetype)initwithBonuesArray:(NSArray *)array {
    if (self == [super initWithNibName:@"QSU15BonusViewController" bundle:nil]) {
        _bonusArray = array;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self _configNav];
    [self _configUI];
    [self _bindVCWithArray:_bonusArray];
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[QSUnreadManager getInstance] clearBonuUnread];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (void)bonusListBtnPressed:(id)sender
{
    QSU16BonusListViewController *vc = [[QSU16BonusListViewController alloc]init];
    vc.listArray = _bonusArray;
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)shareToGetBonusBtnPressed:(id)sender {
#warning TODO
    if (_currMoney != 0) {
        NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
        NSString *peopleId = [QSPeopleUtil getPeopleId:peopleDic];
        
        __weak QSU15BonusViewController *weakSelf = self;
#warning TODO Refactor
        [SHARE_NW_ENGINE shareCreateBonus:peopleId onSucceed:^(NSDictionary *shareDic) {
            [[QSShareService shareService]shareWithWechatMoment:[QSShareUtil getShareTitle:shareDic] desc:[QSShareUtil getShareDesc:shareDic] image:[UIImage imageNamed:@"share_icon"] url:[QSShareUtil getshareUrl:shareDic] onSucceed:^{
                [SHARE_NW_ENGINE getBonusWithAlipayId:self.alipayId OnSusscee:^{
                    NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
                    NSArray *bonusArray = [QSPeopleUtil getBonusList:peopleDic];
                    [weakSelf _bindVCWithArray:bonusArray];
                    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"佣金提取成功 款项将会在48小时内转至您的账户" delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
                    alert.tag = 345;
                    [alert show];
                } onError:^(NSError *error) {
                    
                }];
            } onError:nil];
        } onError:^(NSError *error) {
            
        }];
    }
}
- (IBAction)faqBtnPressed:(id)sender {
}
- (IBAction)closeFaqBtnPressed:(id)sender {
}


#pragma mark - AlertView Delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView.tag == 345) {
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
}

#pragma mark - Private
- (void)_configUI {
    self.shareToGetBtn.layer.cornerRadius = 30.f/2;
    self.scrollView.scrollEnabled = YES;
    
    self.containerView.contentSize = self.bonusContentView.bounds.size;
    [self.containerView addSubview:self.bonusContentView];
}

- (void)_configNav {
    self.title = @"佣金账户";
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc]initWithTitle:@"佣金明细" style:UIBarButtonItemStyleDone target:self action:@selector(bonusListBtnPressed:)];
    self.navigationItem.rightBarButtonItem = rightItem;
}

- (void)_bindVCWithArray:(NSArray*)bonusArray {
    if (bonusArray.count) {
        _currMoney = 0;
        float money = 0;
        for (NSDictionary *dic in bonusArray) {
            money += [QSPeopleUtil getMoneyFromBonusDict:dic].floatValue;
            if ([QSPeopleUtil getStatusFromBonusDict:dic].integerValue == 0) {
                _currMoney += [QSPeopleUtil getMoneyFromBonusDict:dic].floatValue;
            }
        }
        if (_currMoney  ==  0) {
            self.shareToGetBtn.backgroundColor = [UIColor lightGrayColor];
            self.shareToGetBtn.userInteractionEnabled = NO;
            self.navigationItem.rightBarButtonItem.action = nil;
        }
        self.currBonusLabel.text = [NSString stringWithFormat:@"￥%.2f",_currMoney];
        self.allBonusLabel.text = [NSString stringWithFormat:@"￥%.2f",money];
    }
}


@end
