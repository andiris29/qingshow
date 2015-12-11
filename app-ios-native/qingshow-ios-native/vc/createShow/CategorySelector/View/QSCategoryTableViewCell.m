//
//  QSS21TableViewCell.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSCategoryTableViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSS21ItemView.h"
#import "QSS21TableViewProvider.h"
#import "QSCategoryUtil.h"

#define seletedColor [UIColor colorWithRed:234/255.0 green:128/255.0 blue:146/255.0 alpha:1.0] 
#define kItemWith 94.0
#define kItemHeight 117.0

@interface QSCategoryTableViewCell ()

@end
@implementation QSCategoryTableViewCell
- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark -- 左右箭头触发事件
- (IBAction)changePage:(UIButton *)sender {
    CGFloat contentOffsetX = self.scrollView.contentOffset.x;
    [UIView animateWithDuration:0.3 animations:^{
        if (sender.tag) {
            if (contentOffsetX > self.scrollView.contentSize.width - kItemWith*3) {
                return;
            }
            self.scrollView.contentOffset = CGPointMake(contentOffsetX + kItemWith, 0);
        }else{
            if (contentOffsetX < kItemWith || contentOffsetX == kItemWith) {
                self.scrollView.contentOffset = CGPointMake(0, 0);
                return;
            }
            self.scrollView.contentOffset = CGPointMake(contentOffsetX - kItemWith, 0);
        }
    }];
    
}

#pragma mark -- 设置子控件
- (void)setSubViewsWith:(NSDictionary *)cellDic andSelectedDic:(NSDictionary *)selectedDic
{
    //设置title圆角
    [self setTitleButtonCornerRadius];
    
    NSString *titleStr = [QSCategoryUtil getName:cellDic];
    [self.titleButton setTitle:titleStr forState:UIControlStateNormal];
    NSArray *array = [QSCategoryUtil getChildren:cellDic];
    [self setItemsWith:array select:selectedDic];
}
- (void)setLastCellWith:(NSDictionary *)cellDic andSelectedArray:(NSArray *)selectedArray
{
    [self setTitleButtonCornerRadius];
    NSString *titleStr = [QSCategoryUtil getName:cellDic];
    [self.titleButton setTitle:titleStr forState:UIControlStateNormal];
    
    NSArray *array = [QSCategoryUtil getChildren:cellDic];
    [self setItemForLastCell:array selectedArray:selectedArray];
}
#pragma mark -- 设置scrollView item

- (void)setItemForLastCell:(NSArray *)array selectedArray:(NSArray *)selectedArray
{
    self.scrollView.contentSize = CGSizeMake(array.count*kItemWith, kItemHeight);
    for (QSS21ItemView *item in self.scrollView.subviews) {
        [item removeFromSuperview];
    }
    for (int i = 0; i < array.count; i ++) {
        
        //初始化resultArray
        //从nib记载自定义button
        NSArray *nibViews = [[NSBundle mainBundle] loadNibNamed:@"QSS21ItemView" owner:self options:nil];
        
        QSS21ItemView *item = [nibViews lastObject];
        item.frame = CGRectMake(i *kItemWith, 0, 64, kItemHeight);
        
        //添加点击手势
        UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(changeitemState:)];
        singleTap.numberOfTapsRequired = 1;
        [item addGestureRecognizer:singleTap];
        
        //设置图片和title
        NSDictionary *itemDic = array[i];
        item.itemDic = itemDic;
        
        [item setLastCellItem:selectedArray];
        
        [self.scrollView addSubview:item];
        
    }

}
- (void)setItemsWith:(NSArray *)array select:(NSDictionary*)dict
{
    self.scrollView.contentSize = CGSizeMake(array.count*kItemWith, kItemHeight);
    for (QSS21ItemView *item in self.scrollView.subviews) {
        [item removeFromSuperview];
    }
    for (int i = 0; i < array.count; i ++) {
        
        //初始化resultArray
        //从nib记载自定义button
        NSArray *nibViews = [[NSBundle mainBundle] loadNibNamed:@"QSS21ItemView" owner:self options:nil];
        
        QSS21ItemView *item = [nibViews lastObject];
        item.frame = CGRectMake(i *kItemWith, 0, 64, kItemHeight);
        
        //添加点击手势
        UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(changeitemState:)];
        singleTap.numberOfTapsRequired = 1;
        [item addGestureRecognizer:singleTap];
        
        //设置图片和title
        NSDictionary *itemDic = array[i];
        item.itemDic = itemDic;
        
        [item setSubViewsValueWith:dict];
        
        [self.scrollView addSubview:item];
        
    }
    
}

#pragma mark -- item触发事件
- (void)changeitemState:(UITapGestureRecognizer *)gesture
{
    QSS21ItemView *item = (QSS21ItemView *)gesture.view;
    if ([self.delegate respondsToSelector:@selector(didSelectItem:ofCell:)]) {
        [self.delegate didSelectItem:item.itemDic ofCell:self];
    }
}
#pragma mark -- titleButton 设置圆角
- (void)setTitleButtonCornerRadius
{
    self.titleButton.layer.cornerRadius =  self.titleButton.frame.size.width/6.0;
}

@end
