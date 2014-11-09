//
//  UIViewController+ShowErrorHud.h
//  hebeibocai
//
//  Created by wxy325 on 13-8-30.
//  Copyright (c) 2013年 com.miaomiaobase. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIViewController (ShowHud)

- (void)showTextHud:(NSString*)text;
- (void)showErrorHudWithText:(NSString*)text;
- (void)showSuccessHudWithText:(NSString*)text;
@end
