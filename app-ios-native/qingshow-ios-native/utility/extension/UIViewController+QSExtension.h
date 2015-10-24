//
//  UIViewController+Network.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define kShowLoginPrompVcNotificationName @"kShowLoginPrompVcNotificationName"
#define kHideLoginPrompVcNotificationName @"kHideLoginPrompVcNotificationName"
@interface UIViewController(Network)

- (void)handleError:(NSError*)error;
- (void)showShowDetailViewController:(NSDictionary*)showDict;
- (void)showLoginPrompVc;
- (void)hideLoginPrompVc;
- (void)hideNaviBackBtnTitle;
- (void)disableAutoAdjustScrollViewInset;

- (void)showItemDetailViewController:(NSDictionary*)itemDict peopleId:(NSString *)peopleId;

@end
