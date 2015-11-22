//
//  QSS21ItemView.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS21ItemView : UIView

@property (weak, nonatomic) IBOutlet UIImageView *imgView;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@property (strong  ,nonatomic)NSDictionary *itemDic;

//设置item的值
- (void)setSubViewsValueWith:(NSDictionary *)selectedDic;

- (void)setLastCellItem:(NSArray *)array;
@end
