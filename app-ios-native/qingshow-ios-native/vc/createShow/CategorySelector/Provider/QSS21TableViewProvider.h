//
//  QSS21TableViewProvider.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSCategoryTableViewCell.h"
@class QSS21TableViewProvider;


@interface QSS21TableViewProvider : QSTableViewBasicProvider<QSCategoryTableViewCellDelegate>


@property (strong , nonatomic) NSArray *dataArray;
@property (strong , nonatomic) NSMutableArray *selectedArray;

//获取cell的记录结果
- (NSMutableArray *)getResultArray;
@property (strong ,nonatomic) NSMutableArray *cellArray;
@end
