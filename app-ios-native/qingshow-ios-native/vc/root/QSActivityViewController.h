//
//  QSActivityViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/3.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSActivityViewController;
@protocol QSActivityViewControllerDelegate <NSObject>

- (void)activityVcShouldDismiss:(QSActivityViewController*)vc;

@end

@interface QSActivityViewController : UIViewController

@property (weak, nonatomic) NSObject<QSActivityViewControllerDelegate>* delegate;
- (instancetype)initWithImgPath:(NSString*)path;

@end
