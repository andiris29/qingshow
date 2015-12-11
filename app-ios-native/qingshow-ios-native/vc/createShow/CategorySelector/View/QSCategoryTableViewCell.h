//
//  QSS21TableViewCell.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@class QSS21TableViewProvider;

@class QSCategoryTableViewCell;

@protocol QSCategoryTableViewCellDelegate <NSObject>

- (void)didSelectItem:(NSDictionary*)itemDict ofCell:(QSCategoryTableViewCell*)cell;

@end

@interface QSCategoryTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIButton *titleButton;

@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) NSObject<QSCategoryTableViewCellDelegate>* delegate;

//设置cell的子控件
- (void)setSubViewsWith:(NSDictionary *)cellDic andSelectedDic:(NSDictionary *)selectedDic;
- (void)setLastCellWith:(NSDictionary *)cellDic andSelectedArray:(NSArray *)selectedArray;
@end
