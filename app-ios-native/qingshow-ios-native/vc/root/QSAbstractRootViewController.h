//
//  QSAbstractRootViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSRootMenuView.h"
#import "QSG02WelcomeViewController.h"

@interface QSAbstractRootViewController : UIViewController <QSRootMenuViewDelegate, QSG02WelcomeViewControllerDelegate>

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;
- (void)hideMenu;

@end
