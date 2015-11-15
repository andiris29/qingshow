//
//  QSU07RegisterViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/14.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU07RegisterViewController : UIViewController<UITextFieldDelegate>
@property (nonatomic, assign) id currentResponder;

@property (strong, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet UIButton *itemBtn;
@property (weak, nonatomic) IBOutlet UIScrollView *containerScrollView;

@end
