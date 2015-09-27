//
//  QSRootContainerViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractRootViewController.h"
#import "QSIRootContentViewController.h"

@interface QSRootContainerViewController : QSAbstractRootViewController <UIAlertViewDelegate>
@property (strong, nonatomic) UINavigationController* contentNavVc;
@property (strong, nonatomic) UIViewController<QSIRootContentViewController>* contentVc;
@end
