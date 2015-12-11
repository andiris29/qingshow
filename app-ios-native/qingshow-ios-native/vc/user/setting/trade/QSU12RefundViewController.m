//
//  QSU12RefundViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/14/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU12RefundViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "QSDateUtil.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSTradeUtil.h"
#import "QSItemUtil.h"
#import "QSNetworkEngine+TradeService.h"
#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"

#define PAGE_ID @"U12 - 申请退货"

@interface QSU12RefundViewController ()<UITextFieldDelegate>

@property (strong, nonatomic) NSDictionary* orderDict;
@property (strong, nonatomic) NSDictionary* receiverDict;
@property (strong, nonatomic) QSU09TradeListViewController *actionVC;

@end

@implementation QSU12RefundViewController

#pragma mark - Init
- (instancetype)initWithDict:(NSDictionary*)orderDict actionVC:(QSU09TradeListViewController *)actionVC
{
    self = [super initWithNibName:@"QSU12RefundViewController" bundle:nil];
    
    if (self) {
        self.orderDict = orderDict;
        self.actionVC = actionVC;
        if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
            self.automaticallyAdjustsScrollViewInsets = NO;
        }
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setPageType];
    [self getAddrWithOrderDict];
    self.widthCon.constant = [UIScreen mainScreen].bounds.size.width;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapView)];
    [self.scrollView addGestureRecognizer:ges];
    [self setCurrentSelectedDate:[NSDate date]];
    
    UITapGestureRecognizer* tapDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapTextField)];
    [self.sendDateTextField addGestureRecognizer:tapDate];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    
    UITapGestureRecognizer* tapGes= [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapPhone:)];
    [self.phoneLabel addGestureRecognizer:tapGes];
    
}
- (void)didTapTextField
{
    [self showPicker];
    
}
- (void)bindWithDic:(NSDictionary *)dict
{
    self.refundAddrLabel.text = [QSItemUtil getReturnAddrFromDic:dict];
    self.companyLabel.text = [QSItemUtil getReturnNameFromDic:dict];
    self.phoneLabel.text = [QSItemUtil getReturnPhoneFromDic:dict];
}
- (void)getAddrWithOrderDict
{
    NSString *tradeId = [QSTradeUtil getTradeId:self.orderDict];
    __weak QSU12RefundViewController *weakself = self;
    [SHARE_NW_ENGINE tradeReceiver:tradeId onSucceed:^(NSDictionary *dict) {
        weakself.receiverDict = dict;
        [weakself bindWithDic:dict];
    } onError:^(NSError *error) {
        
    }];
    
}

- (void)setPageType
{
    self.title  = @"退货方式";
    self.typeAddrLabel.text = @"退货地址:";
    self.typeReceiverLabel.text = @"退货收件人:";
    [self.submitBtn setTitle:@"申请退货" forState:UIControlStateNormal];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Gesture
- (void)didTapView
{
    [self hideKeyboardAndPicker];
}
- (void)hideKeyboardAndPicker
{
    for (UITextField* f in @[self.companyTextField, self.expressOrderTextField, self.resonTextField]) {
        [f resignFirstResponder];
    }
    [self hidePicker];
    [UIView animateWithDuration:0.5f animations:^{
        self.scrollView.contentInset = UIEdgeInsetsZero;
    }];

}
#pragma mark - Keyboard
- (void)configContentInset:(float)height
{
    self.scrollView.contentInset = UIEdgeInsetsMake(0, 0, height, 0);
    [self scrollToBottom:height];
}
- (void)keyboardWillShow:(NSNotification *)notif {
    [self configContentInset:100.f];
}

- (void)keyboardWillHide:(NSNotification *)notif {
    [UIView animateWithDuration:0.5f animations:^{
        self.scrollView.contentInset = UIEdgeInsetsZero;
    }];
}

- (void)scrollToBottom:(float)keyboardHeight
{
    [self.scrollView setContentOffset:CGPointMake(0, keyboardHeight) animated:YES];
}


- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    [self configContentInset:100.0f];
}
- (void)textFieldDidEndEditing:(UITextField *)textField
{
    NSString *resonStr = @"";
    if (self.resonTextField.text) {
        resonStr = self.resonTextField.text;
    }
     [self.orderDict setValue:resonStr forKey:@"comments"];
    [self configContentInset:-300.0f];
        [self.view resignFirstResponder];
}

- (IBAction)submitBtnPressed:(id)sender {
    if (!self.companyTextField.text.length) {
        [self showErrorHudWithText:@"请填写快递公司"];
        return;
    }
    if (!self.expressOrderTextField.text.length) {
        [self showErrorHudWithText:@"请填写物流单号"];
        return;
    }
    
    [self hideKeyboardAndPicker];
    [SHARE_NW_ENGINE tradeReturn:self.orderDict company:self.companyTextField.text trackingId:self.expressOrderTextField.text comment:self.resonTextField.text onSucceed:^(NSDictionary* dict){
        [self showTextHud:@"申请成功"];
        [self performSelector:@selector(popBack) withObject:nil afterDelay:TEXT_HUD_DELAY];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
- (void)popBack
{
    [self.actionVC.provider reloadData];
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - Picker

- (IBAction)pickerValueChanged:(UIDatePicker *)sender {
    NSDate* date = sender.date;
    [self setCurrentSelectedDate:date];
}
- (void)showPicker
{
    if (!self.datePicker.hidden) {
        return;
    }
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromTop;
    [self.datePicker.layer addAnimation:tran forKey:@"show"];
    self.datePicker.hidden = NO;
    [self configContentInset:100];
}
- (void)hidePicker
{
    if (self.datePicker.hidden) {
        return;
    }
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromBottom;
    [self.datePicker.layer addAnimation:tran forKey:@"hide"];
    self.datePicker.hidden = YES;
}
- (void)setCurrentSelectedDate:(NSDate*)date
{
    self.sendDateTextField.text = [QSDateUtil buildDateStringFromDate:date];
}

- (void)didTapPhone:(UITapGestureRecognizer*)ges {
    if (!self.receiverDict) {
        return;
    }
    NSString* phone = [QSItemUtil getReturnPhoneFromDic:self.receiverDict];
    if (!phone || !phone.length) {
        return;
    }
    NSString* str = [[NSString alloc] initWithFormat:@"telprompt://%@",phone];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:str]];
    
}
@end
