//
//  QSU11LocationEditingViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/14/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU11ReceiverEditingViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSReceiverUtil.h"
#import "UIViewController+QSExtension.h"
#import "QSUserManager.h"
#import "QSPeopleUtil.h"
#define PAGE_ID @"U11 - 编辑收获地址"

@interface QSU11ReceiverEditingViewController ()

@property (strong, nonatomic) NSArray* cellArray;
@property (strong, nonatomic) NSArray* textFieldArray;

@property (strong, nonatomic) NSDictionary* locationDict;
@property (strong, nonatomic) NSString* selectionLocation;
@property (strong, nonatomic) QSLocationPickerProvider* pickerProvider;
@property (strong, nonatomic) NSTimer *timer;
@end

@implementation QSU11ReceiverEditingViewController
- (void)setSelectionLocation:(NSString *)selectionLocation
{
    _selectionLocation = selectionLocation;
    self.localLabel.text = _selectionLocation;
}

#pragma mark - Init
- (instancetype)initWithDict:(NSDictionary*)dict
{
    self = [super initWithNibName:@"QSU11ReceiverEditingViewController" bundle:nil];
    if (self) {
        self.locationDict = dict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self configView];
    [self configBarBtn];
    self.pickerProvider = [[QSLocationPickerProvider alloc] initWithPicker:self.provincePicker];
    self.pickerProvider.delegate = self;
    [self bindWithDict:self.locationDict];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - UITableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.cellArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell* cell = self.cellArray[indexPath.row];
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        cell.separatorInset = UIEdgeInsetsZero;
    }
    if ([cell respondsToSelector:@selector(setLayoutMargins:)]) {
        cell.layoutMargins = UIEdgeInsetsZero;
    }
    
    return self.cellArray[indexPath.row];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    UITableViewCell* cell = [self.tableView cellForRowAtIndexPath:indexPath];
    if (cell == self.locationCell) {
        [self showPicker];
        [self hideKeyboard];
    }
}

#pragma mark - ScrollView
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [self hideKeyboardAndPicker];
}

#pragma mark - Private
- (void)configView
{
    if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
    
    self.textFieldArray = @[self.nameTextField, self.phoneTextField,self.detailLocationTextField];
    self.cellArray = @[self.nameCell, self.locationCell, self.detailLocationCell, self.phoneCell, self.submitCell];
    UIView* headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 5)];
    headerView.backgroundColor = [UIColor colorWithRed:247.f/255.f green:247.f/255.f blue:247.f/255.f alpha:1.f];
    self.tableView.tableHeaderView = headerView;
    self.tableView.tableFooterView = [[UIView alloc] init];
    self.tableView.backgroundColor = [UIColor colorWithRed:247.f/255.f green:247.f/255.f blue:247.f/255.f alpha:1.f];
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        self.tableView.separatorInset = UIEdgeInsetsZero;
    }
    if ([self.tableView respondsToSelector:@selector(setLayoutMargins:)]) {
        self.tableView.layoutMargins = UIEdgeInsetsZero;
    }
    
    self.view.backgroundColor = [UIColor colorWithRed:247.f/255.f green:247.f/255.f blue:247.f/255.f alpha:1.f];
    
    self.submitBtn.layer.cornerRadius = 4.f;
    self.submitBtn.layer.masksToBounds = YES;

}
- (void)configBarBtn
{
    [self hideNaviBackBtnTitle];
}

- (void)hideKeyboardAndPicker
{
    [self hideKeyboard];
    [self hidePicker];
}
- (void)hideKeyboard
{
    for (UITextField* t in self.textFieldArray) {
        [t resignFirstResponder];
    }
}

- (void)bindWithDict:(NSDictionary*)dict
{
    if (dict) {
        self.title = @"编辑地址";
    } else {
        self.title = @"新增地址";
    }
    
    self.nameTextField.text = [QSReceiverUtil getName:dict];
    self.phoneTextField.text = [QSReceiverUtil getPhone:dict];
    self.localLabel.text = [QSReceiverUtil getProvince:dict];
    [self.pickerProvider bindWithValue:self.localLabel.text];
    self.selectionLocation = self.localLabel.text;
    self.detailLocationTextField.text = [QSReceiverUtil getAddress:dict];
}

#pragma mark - IBAction

- (IBAction)submitBtnPressed:(id)sender {
    [self hideKeyboardAndPicker];
    if (!self.nameTextField.text.length || !self.phoneTextField.text.length || !self.detailLocationTextField.text.length) {
        [self showErrorHudWithText:@"请填写完整信息"];
        return;
    }
    
    [self hideKeyboardAndPicker];
    NSString* uuid = nil;
    BOOL isDefault = YES;
    if (self.locationDict) {
        uuid = [QSReceiverUtil getUuid:self.locationDict];
        isDefault = NO;
    }
    
    [SHARE_NW_ENGINE saveReceiver:uuid
                             name:self.nameTextField.text
                            phone:self.phoneTextField.text
                         province:self.selectionLocation
                          address:self.detailLocationTextField.text
                        isDefault:isDefault
                        onSuccess:^(NSDictionary *people, NSString *uuid, NSDictionary *metadata)
     {
         [self showTextHud:@"保存成功"];
         [self performSelector:@selector(popBack) withObject:nil afterDelay:TEXT_HUD_DELAY];
     } onError:^(NSError *error) {
         [self handleError:error];
     }];
}

- (void)popBack
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - QSProvinceSelectionTableViewControllerDelegate
- (void)provinceSelectionVc:(QSProvinceSelectionTableViewController*)vc didSelectionProvince:(NSString*)province city:(NSString*)city
{
    self.selectionLocation = [NSString stringWithFormat:@"%@ %@", province, city];
}


- (void)showPicker
{
    if (!self.provincePicker.hidden) {
        return;
    }
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromTop;
    [self.provincePicker.layer addAnimation:tran forKey:@"show"];
    self.provincePicker.hidden = NO;
}
- (void)hidePicker
{
    if (self.provincePicker.hidden) {
        return;
    }
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromBottom;
    [self.provincePicker.layer addAnimation:tran forKey:@"hide"];
    self.provincePicker.hidden = YES;
}

- (void)locationValueChange:(QSLocationPickerProvider*)provider
{
    self.selectionLocation = [provider getSelectedValue];
}
@end
