//
//  QSVersionUpdateAlertHandler.h
//  qingshow-ios-native
//
//  Created by wxy325 on 9/2/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSVersionUpdateAlertHandler : NSObject

@property (strong, nonatomic) UIAlertView* alertView;

- (id)initWithVc:(UIViewController*)vc alertView:(UIAlertView*)alertView;

@end
