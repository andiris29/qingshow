//
//  QSShareViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSShareViewControllerDelegate <NSObject>

- (void)didShareWeiboSuccess;

@end

@interface QSShareViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIView* shareContainer;
@property (weak, nonatomic) IBOutlet UIView* sharePanel;
- (void)showSharePanel;
- (void)hideSharePanel;
@property (weak, nonatomic) NSObject<QSShareViewControllerDelegate>* delegate;

@end
