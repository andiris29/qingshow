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


//Show
@property (weak, nonatomic) IBOutlet UIImageView *showImageView;

@property (weak, nonatomic) IBOutlet UIButton *showButton;

@property (weak, nonatomic) IBOutlet UIImageView *shouShadowImageView;

//Item
@property (strong, nonatomic) IBOutletCollection(UIImageView) NSArray *itemImageViews;

@property (strong, nonatomic) IBOutletCollection(UIButton) NSArray *itemButtons;

//阴影
@property (strong ,nonatomic)IBOutletCollection(UIImageView)NSArray *shadow;

- (void)bindWithShow:(NSDictionary*)showDict;

@end
