//
//  QSS20MatcherViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/21/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSMatcherItemSelectionView.h"
#import "QSMatcherCanvasView.h"
#import "QSS21CategorySelectorVC.h"
#import "QSMatcherItemsProvider.h"

@protocol QSMenuProviderDelegate;

@interface QSS20MatcherViewController : UIViewController <QSMatcherCanvasViewDelegate, QSMatcherItemsProviderDelegate, QSS21CategorySelectorVCDelegate>

- (instancetype)init;

@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;
@property (weak, nonatomic) IBOutlet UIView *itemSelectionContainer;
@property (weak, nonatomic) IBOutlet UIView *canvasContainer;

- (IBAction)categorySelectedBtnPressed:(id)sender;

- (IBAction)menuBtnPressed:(id)sender;
- (IBAction)submitButtonPressed:(id)sender;

@property (weak, nonatomic) IBOutlet UIButton *submitButton;
@property (weak, nonatomic) IBOutlet UIButton *categorySelectionButton;


@end
