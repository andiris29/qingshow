//
//  QSCommentListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommentListViewController.h"
#import "QSCommentTableViewCell.h"

@interface QSCommentListViewController ()

@property (strong, nonatomic) IBOutlet UITableView* tableView;

@end

@implementation QSCommentListViewController

- (id)init
{
    self = [self initWithNibName:@"QSCommentListViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.tableView registerNib:[UINib nibWithNibName:@"QSCommentTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSCommentTableViewCell"];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITable View
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 10;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSCommentTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"QSCommentTableViewCell" forIndexPath:indexPath];
    return cell;
}
- (float)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 62.f;
}



@end
