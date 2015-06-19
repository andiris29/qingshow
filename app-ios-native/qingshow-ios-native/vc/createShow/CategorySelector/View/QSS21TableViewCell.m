//
//  QSS21TableViewCell.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21TableViewCell.h"
#import "QSS21ItemButton.h"
#define seletedColor [UIColor colorWithRed:234/255.0 green:128/255.0 blue:146/255.0 alpha:1.0] 

@implementation QSS21TableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark -- 左右箭头触发事件
- (IBAction)changePage:(UIButton *)sender {
    
}

#pragma mark -- 设置子控件
- (void)setSubViewsWith:(NSDictionary *)cellDic
{
    //设置title圆角
    [self setTitleButtonCornerRadius];
    
    NSArray *array = cellDic[@""];
    [self setItemsWith:array];
}

#pragma mark -- 设置scrollView item
- (void)setItemsWith:(NSArray *)array
{
    self.scrollView.contentSize = CGSizeMake(array.count*94, 117);
    for (int i = 0; i < 7; i ++) {
        
        //初始化resultArray
        //从nib记载自定义button
        NSArray *nibViews = [[NSBundle mainBundle] loadNibNamed:@"QSS21ItemButton" owner:self options:nil];
        
        QSS21ItemButton *item = [nibViews lastObject];
        item.frame = CGRectMake(i *94, 0, 64, 117);
        
        [item addTarget:self action:@selector(changeitemState:) forControlEvents:UIControlEventTouchUpInside];
#warning TODO 设置item的title image （normal/selected）
        [item setTitle:@"哈哈" forState:UIControlStateNormal];
//        [item setTitle:@"哈哈" forState:UIControlStateSelected];
//
//        [item setImage:[UIImage imageNamed:@"baidi"] forState:UIControlStateNormal];
//        [item setImage:[UIImage imageNamed:@"hongdi"] forState:UIControlStateNormal];
        
        [self.scrollView addSubview:item];
    }
}

#pragma mark -- item触发事件
- (void)changeitemState:(UIButton *)item
{
    UIScrollView *scroll = (UIScrollView *)item.superview;
    NSArray *items = scroll.subviews;
    
    for (int i = 0; i < items.count; i ++) {
        QSS21ItemButton *itemBT = items[i];
        if (itemBT.tag) {
            itemBT.selected = NO;
        }
    }
#warning  TODO //更改数据传值 可以为cell 加一个属性 给provider观察者
    item.selected = YES;
    
}
#pragma mark -- titleButton 设置圆角
- (void)setTitleButtonCornerRadius
{
    self.titleButton.layer.cornerRadius =  self.titleButton.frame.size.width/6.0;
}


@end
