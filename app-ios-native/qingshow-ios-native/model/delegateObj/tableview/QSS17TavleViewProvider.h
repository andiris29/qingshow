//
//  QSS17TavleViewProvider.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSS17TopShowCell.h"

@protocol QSS17ProviderDelegate <NSObject>

- (void)tableViewCellDidClicked:(NSInteger)row;

@end

@interface QSS17TavleViewProvider : QSTableViewBasicProvider

@property(nonatomic,strong)NSArray *dataArray;
@property(nonatomic,weak)NSObject<QSS17ProviderDelegate>* delegate;
- (id)initWithArray:(NSArray *)array;

@end
