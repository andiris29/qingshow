//
//  QSU10UserLocationListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU10UserLocationListViewController.h"
#import "QSU11LocationEditingViewController.h"

@interface QSU10UserLocationListViewController ()

@property (strong, nonatomic) NSArray* locationArray;

@end

@implementation QSU10UserLocationListViewController

#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSU10UserLocationListViewController" bundle:nil];
    if (self) {
        [self buildTestData];
    }
    return self;
}

- (void)buildTestData
{
    NSMutableArray* array = [@[] mutableCopy];
    [array addObject:@{ @"name" : @"黄小仙",
                       @"phone" : @"13332223334",
                       @"privince" : @"上海",
                       @"address" : @"上海市普陀区中山北路号楼层",
                       @"default" : @""
                        }];
    [array addObject:@{ @"name" : @"黄小仙",
                        @"phone" : @"13332223334",
                        @"privince" : @"上海",
                        @"address" : @"上海市普陀区中山北路号楼层",
                        @"default" : @""
                        }];
    [array addObject:@{ @"name" : @"黄小仙",
                        @"phone" : @"13332223334",
                        @"privince" : @"上海",
                        @"address" : @"上海市普陀区中山北路号楼层",
                        @"default" : @""
                        }];
    self.locationArray = array;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.tableView registerNib:[UINib nibWithNibName:@"QSUserLocationTableViewCell" bundle:nil] forCellReuseIdentifier:QSUserLocationTableViewCellIdentifier];
    self.title = @"收获地址管理";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithTitle:@"新增地址" style:UIBarButtonItemStylePlain target:self action:@selector(newLocationBtnPressed)];
    item.tintColor = [UIColor colorWithRed:169.f/255.f green:26.f/255.f blue:78.f/255.f alpha:1.f];
    self.navigationItem.rightBarButtonItem = item;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableView Datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.locationArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSUserLocationTableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:QSUserLocationTableViewCellIdentifier forIndexPath:indexPath];
    cell.delegate = self;
    NSDictionary* dict = [self locationDictForIndexPath:indexPath];
    [cell bindWithDict:dict];
    return cell;
}

#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return QSUserLocationTableViewCellHeight;
}

#pragma mark - QSUserLocationTableViewCellDelegate
- (void)didClickEditButtonOfCell:(QSUserLocationTableViewCell*)cell
{
    NSDictionary* dict = [self locationDictForCell:cell];
    UIViewController* vc = [[QSU11LocationEditingViewController alloc] initWithDict:dict];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)didClickDeleteButtonOfCell:(QSUserLocationTableViewCell*)cell
{

}
- (void)didClickSelectedIndicatorOfCell:(QSUserLocationTableViewCell*)cell
{

}

#pragma mark - Private
- (void)newLocationBtnPressed
{
    UIViewController* vc = [[QSU11LocationEditingViewController alloc] initWithDict:nil];
    [self.navigationController pushViewController:vc animated:YES];
}
- (NSDictionary*)locationDictForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger row = indexPath.row;
    if (row < self.locationArray.count) {
        return self.locationArray[row];
    } else {
        return nil;
    }
}

- (NSDictionary*)locationDictForCell:(UITableViewCell*)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    return [self locationDictForIndexPath:indexPath];
}
@end
