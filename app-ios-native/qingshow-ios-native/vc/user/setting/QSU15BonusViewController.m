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
@interface QSU15BonusViewController ()

@end

@implementation QSU15BonusViewController
{
    NSArray *_bonusArray;
    NSString *_alipayStr;
}


- (instancetype)initwithBonuesArray:(NSArray *)array
{
    if (self == [super initWithNibName:@"QSU15BonusViewController" bundle:nil]) {
        _bonusArray = array;
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configNav];
    [self configUI];
    [self bindVCWithArray:_bonusArray];
}

- (void)bindVCWithArray:(NSArray*)bonusArray;
{
    if (bonusArray.count) {
        float currMoney = 0;
        float money = 0;
        for (NSDictionary *dic in bonusArray) {
            money += [QSPeopleUtil getMoneyFromBonusDict:dic].floatValue;
            if ([QSPeopleUtil getStatusFromBonusDict:dic].integerValue == 0) {
                currMoney += [QSPeopleUtil getMoneyFromBonusDict:dic].floatValue;
            }
        }
        self.currBonusLabel.text = [NSString stringWithFormat:@"￥%.2f",currMoney];
        self.allBonusLabel.text = [NSString stringWithFormat:@"￥%.2f",money];
    }
}
- (void)configUI
{
    self.shareToGetBtn.layer.cornerRadius = 30.f/2;
    self.scrollView.delegate = self;
    self.scrollView.scrollEnabled = YES;
    self.alipayTextField.delegate = self;
    if (self.alipayId.length > 1) {
        self.alipayTextField.text = self.alipayId;
    }
   
}
- (void)configNav
{
    self.title = @"佣金账户";
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc]initWithTitle:@"佣金明细" style:UIBarButtonItemStyleDone target:self action:@selector(didClickBonuesInfo)];
    self.navigationItem.rightBarButtonItem = rightItem;
}
- (void)didClickBonuesInfo
{
    QSU16BonusListViewController *vc = [[QSU16BonusListViewController alloc]init];
    vc.listArray = _bonusArray;
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}
#pragma mark - textFieldDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    CGPoint p = CGPointMake(0, 0);
    p.y += 60;
    [self.scrollView setContentOffset:p animated:YES];
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    _alipayStr = textField.text;
    CGPoint p = CGPointMake(0, 0);
    [self.scrollView setContentOffset:p animated:YES];
    [self.alipayTextField resignFirstResponder];
    return YES;
}

- (IBAction)shareToGetBonusBtnPressed:(id)sender {
    
    if (![self.alipayTextField.text isEqualToString:@""] && (self.alipayTextField.text != nil) ) {
        self.alipayId = self.alipayTextField.text;
        __weak QSU15BonusViewController *weakSelf = self;
#warning TODO Refactor
        [[QSShareService shareService]shareWithWechatMoment:@"原来玩搭配还能赚钱，我觉得我快要发财了..." desc:@"只要其他用户通过你的美搭购买了其中的单品,丰厚佣金即刻转账至您的账户" image:[UIImage imageNamed:@"share_icon"] url:[NSString stringWithFormat:@"%@?entry=shareBonus&initiatorRef=%@",[QSShareService getShareHost],self.peopleId] onSucceed:^{
            [SHARE_NW_ENGINE getBonusWithAlipayId:self.alipayId OnSusscee:^{
                NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
                NSArray *bonusArray = [QSPeopleUtil getBonusList:peopleDic];
                [weakSelf bindVCWithArray:bonusArray];
                UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"佣金提取成功 款项将会在48小时内转至您的账户" delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
                alert.tag = 345;
                [alert show];
            } onError:^(NSError *error) {
                
            }];
        } onError:nil];
    }else{
        [self showTextHud:@"请填写支付宝账号!"];
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView.tag == 345) {
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
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

@end
