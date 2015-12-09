//
//  QSShareViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSShareViewControllerDelegate <NSObject>

@optional
- (void)didShareWeiboSuccess;
- (void)didShareWechatSuccess;

@end

@interface QSShareViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIView* shareContainer;
@property (weak, nonatomic) IBOutlet UIView* sharePanel;
- (void)showSharePanelWithTitle:(NSString*)title desc:(NSString*)desc url:(NSString*)urlStr shareIconUrl:(NSString*)shareIconUrl;
- (void)hideSharePanel;
@property (weak, nonatomic) NSObject<QSShareViewControllerDelegate>* delegate;

@end
