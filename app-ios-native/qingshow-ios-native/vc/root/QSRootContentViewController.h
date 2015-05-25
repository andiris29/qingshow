//
//  QSRootContentViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@protocol QSMenuProviderDelegate;

@interface QSRootContentViewController : UIViewController

@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;

@end
