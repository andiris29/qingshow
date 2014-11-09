//
//  QSTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelListTableViewDelegateObj.h"

@interface QSModelListTableViewDelegateObj ()

@property (assign, nonatomic) Class cellClass;
@property (strong, nonatomic) UINib* cellNib;
@property (strong, nonatomic) NSString* identifier;
@property (strong, nonatomic) UITableView* tableView;

@end

@implementation QSModelListTableViewDelegateObj

#pragma mark - Init
- (id)initWithCellClass:(Class)cellClass identifier:(NSString*)identifier
{
    self = [self initWIthIdentifier:identifier];
    if (self) {
        self.cellClass = cellClass;
    }
    return self;
}

- (id)initWithCellNib:(UINib*)cellNib identifier:(NSString*)identifier
{
    self = [self initWIthIdentifier:identifier];
    if (self) {
        self.cellNib = cellNib;
    }
    return self;
}
- (id)initWIthIdentifier:(NSString*)identifier
{
    self = [self init];
    if (self) {
        self.identifier = identifier;
    }
    return self;
}
- (id)init
{
    self = [super init];
    if (self) {
        self.resultArray = [@[] mutableCopy];
    }
    return self;
}

#pragma mark - Bind
- (void)bindWithTableView:(UITableView*)tableView
{
    self.tableView = tableView;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    [self.tableView registerNib:[UINib nibWithNibName:@"QSModelListTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSModelListTableViewCell"];
}
#pragma mark - Network
- (void)reloadData
{
    [self fetchDataOfPage:1];
}
- (void)fetchDataOfPage:(int)page
{
    self.networkBlock(^(NSArray *array, NSDictionary *metadata) {
        if (page == 1)
        {
            [self.resultArray removeAllObjects];
        }

        [self.resultArray addObjectsFromArray:array];
        [self.tableView reloadData];
    },^(NSError *error) {
        NSLog(@"error");
    },page);
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
    if ([self.delegate respondsToSelector:@selector(clickModel:)]) {
        [self.delegate clickModel:self.resultArray[indexPath.row]];
    }
//    UIViewController* vc = [[QSModelDetailViewController alloc] initWithModel:self.resultArray[indexPath.row]];
//    [self.navigationController pushViewController:vc animated:YES];
}
#pragma mark - QSModelListTableViewCellDelegate
- (void)favorBtnPressed:(QSModelListTableViewCell *)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    if ([self.delegate respondsToSelector:@selector(followBtnPressed:)]) {
        [self.delegate followBtnPressed:self.resultArray[indexPath.row]];
    }
}

#pragma mark - scroll view
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)]) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
}
@end
