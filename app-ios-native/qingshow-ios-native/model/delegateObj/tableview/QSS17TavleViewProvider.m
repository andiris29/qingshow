//
//  QSS17TavleViewProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TavleViewProvider.h"
#import "QSShowUtil.h"

#define SS17CellId @"QSS17TopShowCellId"
#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)

@implementation QSS17TavleViewProvider

@dynamic delegate;

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSS17TopShowCell" bundle:nil] forCellReuseIdentifier:SS17CellId];
}
- (CGFloat)getHeight
{
    if (w == 320 && h == 480) {
        return h/3+10;
    }
    else
    {
    return h/3-13;
    }
}


#pragma mark -UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self getHeight];
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return (self.resultArray.count + 1) / 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSS17TopShowCell *cell = [tableView dequeueReusableCellWithIdentifier:SS17CellId forIndexPath:indexPath];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle]loadNibNamed:@"QSS17TopShowCell" owner:nil options:nil]lastObject];
    }
    cell.userInteractionEnabled = YES;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    NSDictionary* leftDict = self.resultArray[indexPath.row * 2];
    NSDictionary* rightDict = self.resultArray.count > (indexPath.row * 2  + 1)? self.resultArray[indexPath.row * 2 + 1] : nil;
    
    [cell bindWithDataDic:leftDict andAnotherDic:rightDict];
    if (w == 414) {
        cell.contentView.transform  = CGAffineTransformMakeScale(1.3, 1.3);
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDate* date =[QSShowUtil getRecommendDate:self.resultArray[indexPath.row * 2]];
    if ([self.delegate respondsToSelector:@selector(didClickedDate:ofProvider:)]) {
        [self.delegate didClickedDate:date ofProvider:self];
    }
}

#pragma mark --重写bind
- (void)bindWithTableView:(UITableView*)tableView
{
    self.view = tableView;
    self.view.dataSource = self;
    self.view.delegate = self;
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    tableView.showsVerticalScrollIndicator = NO;
    [self registerCell];
}

#pragma mark - Scroll View //可以不写但是从运行效率考虑？？
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    return;
}
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    return ;
}

@end
