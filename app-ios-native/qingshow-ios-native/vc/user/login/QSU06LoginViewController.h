//
//  QSU06LoginViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/11.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU06LoginViewController : UIViewController<UITextFieldDelegate>
@property (nonatomic, assign) id currentResponder;
@property (weak, nonatomic) IBOutlet UILabel *passwordLabel;
@property (weak, nonatomic) UIViewController* previousVc;
- (instancetype)init;
@end
