//
//  QSU14DisplayCell.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSFavoInfo;

@interface QSU14DisplayCell : UITableViewCell

//cell的背景图片
@property (weak, nonatomic) IBOutlet UIImageView *backImage;

//套装图片
@property (weak, nonatomic) IBOutlet UIImageView *suitImageView;
//单品图片
@property (weak, nonatomic) IBOutlet UIImageView *skuImageView1;

@property (weak, nonatomic) IBOutlet UIImageView *skuImageView2;

@property (weak, nonatomic) IBOutlet UIImageView *skuImageView3;

@property (weak, nonatomic) IBOutlet UIImageView *skuImageView4;

//相应控件
@property (weak, nonatomic) IBOutlet UIButton *suitButton;

@property (weak, nonatomic) IBOutlet UIButton *skuButton1;

@property (weak, nonatomic) IBOutlet UIButton *skuButton2;

@property (weak, nonatomic) IBOutlet UIButton *skuButton3;

@property (weak, nonatomic) IBOutlet UIButton *skuButton4;

//cell赋值
- (void)setValueForSubViewsWith:(QSFavoInfo *)favoInfo;
@end
