//
//  QSU14FavoTableVIewProvider.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"

@interface QSU14FavoTableVIewProvider : QSTableViewBasicProvider

@property (nonatomic, strong)NSArray *dataArray;

- (id)initwithArray:(NSArray *)array;
@end
