//
//  UIViewController+ShowErrorHud.m
//  hebeibocai
//
//  Created by wxy325 on 13-8-30.
//  Copyright (c) 2013年 com.miaomiaobase. All rights reserved.
//

#import "UIViewController+ShowHud.h"
#import "MBProgressHUD.h"
#import "QSError.h"

@implementation UIViewController (ShowHud)
- (void)showTextHud:(NSString*)text
{
    MBProgressHUD* hud = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:hud];
    hud.mode = MBProgressHUDModeText;
    hud.removeFromSuperViewOnHide = YES;
    hud.labelText = text;
    [hud show:YES];
    [hud hide:YES afterDelay:1.f];
}

- (void)showErrorHudWithText:(NSString*)text
{
    MBProgressHUD *HUD;
    HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:HUD];
    HUD.labelText = text;
    HUD.mode = MBProgressHUDModeCustomView;
    HUD.removeFromSuperViewOnHide = YES;
    HUD.customView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"exclamationmark"]];
    [HUD show:YES];
    [HUD hide:YES afterDelay:1.f];
}
- (void)showErrorHudWithError:(NSError*)error
{
    if ([error isKindOfClass:[QSError class]]) {
        QSError* qsError = (QSError*)error;
        [self showErrorHudWithText:[qsError toString]];
    }
}

- (void)showSuccessHudWithText:(NSString*)text
{
    MBProgressHUD *HUD;
    HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:HUD];
    HUD.labelText = text;
    HUD.mode = MBProgressHUDModeCustomView;
    HUD.removeFromSuperViewOnHide = YES;
    HUD.customView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Checkmark"]];
    [HUD show:YES];
    [HUD hide:YES afterDelay:1.f];
}

@end
