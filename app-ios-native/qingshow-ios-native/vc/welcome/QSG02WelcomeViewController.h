//
//  QSG02WelcomeViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSG02WelcomeViewController;

@protocol QSG02WelcomeViewControllerDelegate <NSObject>

- (void)dismissWelcomePage:(QSG02WelcomeViewController*)vc;
- (void)presentRegisterVC:(QSG02WelcomeViewController *)vc;
- (void)presentLoginVC:(QSG02WelcomeViewController *)vc;

@end

@interface QSG02WelcomeViewController : UIViewController<UIScrollViewDelegate>
@property (weak, nonatomic) IBOutlet UIButton *loginBtn;
@property (weak, nonatomic) IBOutlet UIPageControl *pageControl;

@property (weak, nonatomic) IBOutlet UIScrollView *welcomeSCV;
@property (weak, nonatomic) IBOutlet UIView *loginAndRegisterView;

@property (weak, nonatomic) NSObject<QSG02WelcomeViewControllerDelegate>* delegate;

- (instancetype)init;

@end
