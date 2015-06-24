//
//  QSS21TableViewCell.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21TableViewCell.h"
#import "QSS21ItemButton.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSS21TableViewProvider.h"

#define seletedColor [UIColor colorWithRed:234/255.0 green:128/255.0 blue:146/255.0 alpha:1.0] 
#define kItemWith 94.0
#define kItemHeight 117.0

@interface QSS21TableViewCell ()

@end
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
- (void)setSubViewsWith:(NSDictionary *)cellDic
{
    //设置title圆角
    [self setTitleButtonCornerRadius];
    
    NSString *titleStr = cellDic[@"name"];
    [self.titleButton setTitle:titleStr forState:UIControlStateNormal];
    
    NSArray *array = cellDic[@"children"];
    [self setItemsWith:array];
}

#pragma mark -- 设置scrollView item
- (void)setItemsWith:(NSArray *)array
{
    self.scrollView.contentSize = CGSizeMake(array.count*kItemWith, kItemHeight);
//    for (QSS21ItemButton *item in self.scrollView.subviews) {
//        [item removeFromSuperview];
//    }
    for (int i = 0; i < array.count; i ++) {
        
        //初始化resultArray
        //从nib记载自定义button
        NSArray *nibViews = [[NSBundle mainBundle] loadNibNamed:@"QSS21ItemButton" owner:self options:nil];
        
        QSS21ItemButton *item = [nibViews lastObject];
        item.frame = CGRectMake(i *kItemWith, 0, 64, kItemHeight);
        
        [item addTarget:self action:@selector(changeitemState:) forControlEvents:UIControlEventTouchUpInside];
        
        //设置item的title image （normal/selected）
        NSDictionary *itemDic = array[i];
        item.itemDic = itemDic;
        NSString *itemName = itemDic[@"name"];
        [item setTitle:itemName forState:UIControlStateNormal];
        [item setTitle:itemName forState:UIControlStateSelected];
       
        NSString *imgUrl = itemDic[@"icon"];
        NSRange range = [imgUrl rangeOfString:@".png"];
        NSString *rangeStr = [imgUrl substringToIndex:range.location];
        NSString *imgSelectedUrl = [NSString stringWithFormat:@"%@_grey.png",rangeStr];
        
        NSData *normalImgData = [NSData dataWithContentsOfURL:[NSURL URLWithString:imgUrl]];
        NSData *selectedImdData = [NSData dataWithContentsOfURL:[NSURL URLWithString:imgSelectedUrl]];
        
        UIImage *norMalImg = [[UIImage alloc] initWithData:normalImgData];
        UIImage *selectedImg = [[UIImage alloc] initWithData:selectedImdData];
        [item setImage:norMalImg forState:UIControlStateNormal];
        [item setImage:selectedImg forState:UIControlStateSelected];
        if (item.itemDic == self.recordDic) {
            item.selected = YES;
        }
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
    QSS21ItemButton *itemButton = (QSS21ItemButton *)item;
    self.recordDic = itemButton.itemDic;
    item.selected = YES;
    
}
#pragma mark -- titleButton 设置圆角
- (void)setTitleButtonCornerRadius
{
    self.titleButton.layer.cornerRadius =  self.titleButton.frame.size.width/6.0;
}


@end
