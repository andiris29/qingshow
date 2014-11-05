//
//  QSModelListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelListViewController.h"
#import "QSModelDetailViewController.h"

#import "QSNetworkEngine.h"

@interface QSModelListViewController ()

@property (strong, nonatomic) NSMutableArray* resultArray;

- (void)configView;
- (void)fetchDataOfPage:(int)page;
@end

@implementation QSModelListViewController

#pragma mark - Init Method
- (id)init
{
    self = [self initWithNibName:@"QSModelListViewController" bundle:nil];
    if (self) {
        self.resultArray = [@[] mutableCopy];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.tableView registerNib:[UINib nibWithNibName:@"QSModelListTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSModelListTableViewCell"];
    
    [self configView];
    [self fetchDataOfPage:1];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - View
- (void)configView
{
    self.navigationItem.title = @"人气达人";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
}

#pragma mark - Network
- (void)fetchDataOfPage:(int)page
{
    [SHARE_NW_ENGINE getModelListPage:page onSucceed:^(NSArray *array, NSDictionary *metadata) {
        [self.resultArray removeAllObjects];
        [self.resultArray addObjectsFromArray:array];
        [self.tableView reloadData];
    } onError:^(NSError *error) {
        NSLog(@"error");
    }];
}

#pragma mark - UITableView DataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.resultArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSModelListTableViewCell* cell = (QSModelListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSModelListTableViewCell" forIndexPath:indexPath];
    cell.delegate = self;
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithPeople:dict];
    
    return cell;
}


#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 62.f;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    UIViewController* vc = [[QSModelDetailViewController alloc] initWithModel:self.resultArray[indexPath.row]];
    [self.navigationController pushViewController:vc animated:YES];
}
#pragma mark - QSModelListTableViewCellDelegate
- (void)favorBtnPressed:(QSModelListTableViewCell *)cell
{
    
}


@end
