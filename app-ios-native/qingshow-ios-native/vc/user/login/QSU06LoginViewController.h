//
//  QSU06LoginViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/11.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU06LoginViewController : UIViewController
@property (weak, nonatomic) IBOutlet UILabel *passwordLabel;

- (id)initWithShowUserDetailAfterLogin:(BOOL)fShowUserDetail;
@end
