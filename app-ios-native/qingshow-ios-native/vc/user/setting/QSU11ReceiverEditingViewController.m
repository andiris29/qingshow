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
#import "QSLocationPickerProvider.h"

@interface QSU11ReceiverEditingViewController ()

@property (strong, nonatomic) NSArray* cellArray;
@property (strong, nonatomic) NSArray* textFieldArray;

@property (strong, nonatomic) NSDictionary* locationDict;
@property (strong, nonatomic) NSString* selectionLocation;
@property (strong, nonatomic) QSLocationPickerProvider* pickerProvider;
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
    [self bindWithDict:self.locationDict];
    self.pickerProvider = [[QSLocationPickerProvider alloc] initWithPicker:self.provincePicker];
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
        QSProvinceSelectionTableViewController* vc = [[QSProvinceSelectionTableViewController alloc] init];
        vc.delegate = self;
        [self.navigationController pushViewController:vc animated:YES];
    }
}

#pragma mark - ScrollView
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [self hideKeyboard];
}

#pragma mark - Private
- (void)configView
{
    if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
    
    self.textFieldArray = @[self.nameTextField, self.phoneTextField, self.detailLocationTextField];
    self.cellArray = @[self.nameCell, self.phoneCell, self.locationCell, self.detailLocationCell];
    
    UIView* headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 5)];
    headerView.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    self.tableView.tableHeaderView = headerView;
    self.tableView.tableFooterView = [[UIView alloc] init];
    self.tableView.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        self.tableView.separatorInset = UIEdgeInsetsZero;
    }
    if ([self.tableView respondsToSelector:@selector(setLayoutMargins:)]) {
        self.tableView.layoutMargins = UIEdgeInsetsZero;
    }
    
    self.view.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    

}
- (void)configBarBtn
{
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithTitle:@"保存" style:UIBarButtonItemStylePlain target:self action:@selector(didSelectSaveBtn)];
    item.tintColor = [UIColor colorWithRed:169.f/255.f green:26.f/255.f blue:78.f/255.f alpha:1.f];
    self.navigationItem.rightBarButtonItem = item;
    [self hideNaviBackBtnTitle];
    
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
    self.detailLocationTextField.text = [QSReceiverUtil getAddress:dict];
}


#pragma mark - IBAction
- (void)didSelectSaveBtn
{
    if (!self.nameTextField.text.length || !self.phoneTextField.text.length || !self.detailLocationTextField.text.length) {
        [self showErrorHudWithText:@"请填写完整信息"];
        return;
    }
    
    [self hideKeyboard];
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
        [self showErrorHudWithError:error];
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
@end
