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

@interface QSU12RefundViewController ()

@property (strong, nonatomic) NSDictionary* orderDict;
@end

@implementation QSU12RefundViewController

#pragma mark - Init
- (instancetype)initWithDict:(NSDictionary*)orderDict
{
    self = [super initWithNibName:@"QSU12RefundViewController" bundle:nil];
    
    if (self) {
        self.orderDict = orderDict;

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
    self.title = @"退货方式";
    self.widthCon.constant = [UIScreen mainScreen].bounds.size.width;
//    ((UIScrollView*)self.view).contentInset = UIEdgeInsetsMake(0, 0, 300.f, 0);
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapView)];
    [self.scrollView addGestureRecognizer:ges];
    [self setCurrentSelectedDate:[NSDate date]];
    
    UITapGestureRecognizer* tapDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapTextField)];
    [self.sendDateTextField addGestureRecognizer:tapDate];
}
- (void)didTapTextField
{
    [self showPicker];
    
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
}
- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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
    for (UITextField* f in @[self.companyTextField, self.expressOrderTextField, self.sendDateTextField]) {
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
    [self configContentInset:300.f];
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

- (IBAction)submitBtnPressed:(id)sender {
    __weak QSU12RefundViewController* weakSelf = self;
    [self hideKeyboardAndPicker];
    [SHARE_NW_ENGINE changeTrade:weakSelf.orderDict status:6 onSucceed:^{
        [SHARE_NW_ENGINE changeTrade:weakSelf.orderDict status:7 onSucceed:^{
            [self showTextHud:@"退款成功"];
            [self performSelector:@selector(popBack) withObject:nil afterDelay:TEXT_HUD_DELAY];
        } onError:^(NSError *error) {
            [self showErrorHudWithError:error];
        }];
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}
- (void)popBack
{
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
@end
