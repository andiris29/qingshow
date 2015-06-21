//
//  QSS20MatcherViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/21/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSMenuProviderDelegate;

@interface QSS20MatcherViewController : UIViewController

- (instancetype)init;

@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;


- (IBAction)menuBtnPressed:(id)sender;

@end
