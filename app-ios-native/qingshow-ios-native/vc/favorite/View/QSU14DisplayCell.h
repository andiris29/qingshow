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
#warning backImage的图片貌似找不到,你有空看一下

#warning 所有图片底部都要加一个阴影，用ImageView上一个阴影图
//套装图片
@property (weak, nonatomic) IBOutlet UIImageView *suitImageView;
//单品图片
@property (weak, nonatomic) IBOutlet UIImageView *skuImageView1;

@property (weak, nonatomic) IBOutlet UIImageView *skuImageView2;

@property (weak, nonatomic) IBOutlet UIImageView *skuImageView3;

@property (weak, nonatomic) IBOutlet UIImageView *skuImageView4;

//相应控件
#warning 文字最好不要用button，或者把button的手势检测禁掉type改成custume，不然点击有阴影。
#warning 文字颜色貌似不对
@property (weak, nonatomic) IBOutlet UIButton *suitButton;

@property (weak, nonatomic) IBOutlet UIButton *skuButton1;

@property (weak, nonatomic) IBOutlet UIButton *skuButton2;

@property (weak, nonatomic) IBOutlet UIButton *skuButton3;

@property (weak, nonatomic) IBOutlet UIButton *skuButton4;

//cell赋值
- (void)setValueForSubViewsWith:(QSFavoInfo *)favoInfo;
@end
