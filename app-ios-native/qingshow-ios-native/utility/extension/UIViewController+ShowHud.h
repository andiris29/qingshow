//
//  UIViewController+ShowErrorHud.h
//  hebeibocai
//
//  Created by wxy325 on 13-8-30.
//  Copyright (c) 2013年 com.miaomiaobase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
#define TEXT_HUD_DELAY 1.f

@interface UIViewController (ShowHud)


- (void)showTextHud:(NSString*)text;
- (void)showErrorHudWithText:(NSString*)text;
- (void)showErrorHudWithError:(NSError*)error;
- (void)showSuccessHudWithText:(NSString*)text;
- (MBProgressHUD*)showNetworkWaitingHud;
@end
