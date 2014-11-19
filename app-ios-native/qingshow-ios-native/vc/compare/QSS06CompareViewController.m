//
//  QSS06CompareViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/18/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS06CompareViewController.h"
#import "QSCompareTableViewCell.h"
@interface QSS06CompareViewController ()

@end

@implementation QSS06CompareViewController
- (id)init
{
    self = [super initWithNibName:@"QSS06CompareViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    [self.tableView registerNib:[UINib nibWithNibName:@"QSCompareTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSCompareTableViewCell"];
    self.tableView.backgroundColor = [UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.title = @"百里挑衣";
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

- (IBAction)typeSegmentValueChanged:(id)sender {
}

#pragma mark - DataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 10;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSCompareTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"QSCompareTableViewCell" forIndexPath:indexPath];
    [cell bindWithDict:nil];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 280.f;
}

@end
