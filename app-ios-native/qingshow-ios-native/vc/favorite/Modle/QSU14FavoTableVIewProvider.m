//
//  QSU14FavoTableVIewProvider.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU14FavoTableVIewProvider.h"
#import "QSU14DisplayCell.h"

#define displayCellId @"DisPlayCellId"

@implementation QSU14FavoTableVIewProvider

- (id)initwithArray:(NSArray *)array
{
    
    if (self) {
        self.dataArray = array;
    }
    return self;
}


- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSU14Display" bundle:[NSBundle mainBundle]] forCellReuseIdentifier:displayCellId];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
//    return self.resultArray.count;
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSU14DisplayCell *cell = [self.view dequeueReusableCellWithIdentifier:@"displayCell"];
    if (cell == nil) {
        cell = [[QSU14DisplayCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"displayCell"];
    }
//    QSFavoInfo *favoInfo = self.resultArray[indexPath.row];
//    [cell setValueForSubViewsWith:favoInfo];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
#warning 这里改成根据屏幕宽度来计算cell高度
//    if (iPhone6Plus) {
//        return 240;
//    }else if (iPhone6){
//        return 225;
//    }else{
//        return 215;
//    }
    return 215;
}

@end
