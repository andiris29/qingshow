//
//  QSS21TableViewCell.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@class QSS21TableViewProvider;

@interface QSS21TableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIButton *titleButton;

@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

//记录每一行选中的item 需要传递的参数
@property (strong , nonatomic) NSDictionary *recordDic;

//设置cell的子控件
- (void)setSubViewsWith:(NSDictionary *)cellDic;

@end
