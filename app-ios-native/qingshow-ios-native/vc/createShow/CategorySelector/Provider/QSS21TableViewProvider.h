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

@protocol QSS21TableViewProviderDelegate <QSAbstractScrollProviderDelegate>
@end

@interface QSS21TableViewProvider : QSTableViewBasicProvider<QSCategoryTableViewCellDelegate>

@property (weak , nonatomic) NSObject<QSS21TableViewProviderDelegate>* delegate;
@property (strong , nonatomic) NSArray *dataArray;
@property (strong , nonatomic) NSMutableArray *selectedArray;
@property (strong, nonatomic) NSDictionary* modelParentDict;

//获取cell的记录结果
- (NSMutableArray *)getResultArray;

@end
