//
//  QSU08PasswordViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/17.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSU08PasswordViewControllerDelegate <NSObject>

- (void)passwordViewController:(QSU08PasswordViewController *)vc
            didSavingPassword:(NSString *)newPassword
            needCurrentPassword:(NSString *)curPasswrod;

@end

@interface QSU08PasswordViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *nowPasswdText;
@property (weak, nonatomic) IBOutlet UITextField *confirmPasswdText;
@property (weak, nonatomic) IBOutlet UITextField *passwdText;

@property (nonatomic, weak) id <QSU08PasswordViewControllerDelegate> delegate;
@end
