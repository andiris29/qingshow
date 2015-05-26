//
//  QSU14FavoTableVIewProvider.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU14FavoTableViewProvider.h"
#import "QSU14DisplayCell.h"

#define displayCellId @"displayCellId"

@implementation QSU14FavoTableViewProvider
@dynamic delegate;

- (id)initwithArray:(NSArray *)array
{
    if (self) {
        self.dataArray = array;
    }
    return self;
}


- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSU14DisplayCell" bundle:nil] forCellReuseIdentifier:displayCellId];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
//    return self.resultArray.count;
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSU14DisplayCell *cell = [tableView dequeueReusableCellWithIdentifier:displayCellId forIndexPath:indexPath];

    NSDictionary* showDict = [self.resultArray lastObject];
    [cell bindWithShow:showDict];
    
    if ([UIScreen mainScreen].bounds.size.width == 414) {
        cell.contentView.transform  = CGAffineTransformMakeScale(1.3, 1.3);
    }
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [UIScreen mainScreen].bounds.size.width/2 + 10;
    
//    return [UIScreen mainScreen].bounds.size.width / 2 / 9 * 16;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSDictionary* showDict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(didSelectionShow:ofProvider:)]) {
        [self.delegate didSelectionShow:showDict ofProvider:self];
    }
}
@end
