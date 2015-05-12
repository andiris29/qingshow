//
//  QSU04EmailViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/17.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSU04EmailViewController;

@protocol QSU04EmailViewControllerDelegate <NSObject>

- (void)emailViewController:(QSU04EmailViewController *)vc
                didSavingEmail:(NSString *)email;
@end


@interface QSU04EmailViewController : UIViewController
@property (weak, nonatomic) IBOutlet UILabel *nowEmailLabel;
@property (weak, nonatomic) IBOutlet UITextField *emailText;
@property (weak, nonatomic) IBOutlet UITextField *confirmEmailText;
@property (weak, nonatomic) id <QSU04EmailViewControllerDelegate> delegate;
@end
