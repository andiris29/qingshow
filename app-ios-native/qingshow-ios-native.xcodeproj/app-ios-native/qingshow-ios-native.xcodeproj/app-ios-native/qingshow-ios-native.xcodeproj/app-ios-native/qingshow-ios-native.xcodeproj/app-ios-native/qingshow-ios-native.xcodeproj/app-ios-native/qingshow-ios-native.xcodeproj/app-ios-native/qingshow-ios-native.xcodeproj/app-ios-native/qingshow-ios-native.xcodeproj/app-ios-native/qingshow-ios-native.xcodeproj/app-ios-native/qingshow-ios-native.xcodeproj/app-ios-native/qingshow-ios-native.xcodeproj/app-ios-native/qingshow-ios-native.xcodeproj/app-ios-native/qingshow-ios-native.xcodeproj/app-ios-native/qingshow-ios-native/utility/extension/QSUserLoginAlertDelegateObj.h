//
//  QSUserLoginAlertDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSUserLoginAlertDelegateObj:NSObject<UIAlertViewDelegate>

@property (weak, nonatomic) UIViewController* vc;
@property (strong, nonatomic) UIAlertView* alertView;

- (id)initWithVc:(UIViewController*)vc alertView:(UIAlertView*)alertView;
@end