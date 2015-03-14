//
//  QSU11LocationEditingViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/14/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU11LocationEditingViewController.h"

@interface QSU11LocationEditingViewController ()

@property (strong, nonatomic) NSArray* cellArray;
@property (strong, nonatomic) NSArray* textFieldArray;

@property (strong, nonatomic) NSDictionary* locationDict;

@end

@implementation QSU11LocationEditingViewController

#pragma mark - Init
- (instancetype)initWithDict:(NSDictionary*)dict
{
    self = [super initWithNibName:@"QSU11LocationEditingViewController" bundle:nil];
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
#warning TODO select location
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
    
#warning TODO
}


#pragma mark - IBAction
- (void)didSelectSaveBtn
{
    
}


@end
