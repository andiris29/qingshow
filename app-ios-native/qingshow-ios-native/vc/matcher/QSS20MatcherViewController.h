//
//  QSS20MatcherViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/21/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSMatcherItemSelectionView.h"

@protocol QSMenuProviderDelegate;

@interface QSS20MatcherViewController : UIViewController <QSMatcherItemSelectionViewDataSource, QSMatcherItemSelectionViewDelegate>

- (instancetype)init;

@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;
@property (weak, nonatomic) IBOutlet UIView *itemSelectionContainer;

- (IBAction)categorySelectedBtnPressed:(id)sender;

- (IBAction)menuBtnPressed:(id)sender;

@end
