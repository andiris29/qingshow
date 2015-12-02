//
//  UIViewController+Network.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface UIViewController(Network)

- (void)handleError:(NSError*)error;
- (void)showShowDetailViewController:(NSDictionary*)showDict;
- (void)hideNaviBackBtnTitle;
- (void)disableAutoAdjustScrollViewInset;

@end
