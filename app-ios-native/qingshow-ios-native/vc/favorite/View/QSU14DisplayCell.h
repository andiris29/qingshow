//
//  QSU14DisplayCell.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU14DisplayCell : UITableViewCell

//cell的背景图片
@property (weak, nonatomic) IBOutlet UIImageView *backImage;

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

//传递VC
@property (strong , nonatomic)UIViewController *currentVC;

@property (strong , nonatomic)NSDictionary *showDict;

@property (strong ,nonatomic)NSArray *itemArray;
@end
