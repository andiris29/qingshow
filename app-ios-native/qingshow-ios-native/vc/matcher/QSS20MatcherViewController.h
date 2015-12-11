//
//  QSS20MatcherViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/21/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QSMatcherCanvasView.h"
#import "QSS21CategorySelectionViewController.h"
#import "QSMatcherItemsProvider.h"
#import "QSS23MatcherPreviewViewController.h"
#import "QSRootContentViewController.h"


@interface QSS20MatcherViewController : UIViewController <QSMatcherCanvasViewDelegate, QSMatcherItemsProviderDelegate, QSS21CategorySelectorVCDelegate, QSS23MatcherPreviewViewControllerDelegate, QSIRootContentViewController>

- (instancetype)init;

@property (weak, nonatomic) IBOutlet UIView *itemSelectionContainer;
@property (weak, nonatomic) IBOutlet UIView *canvasContainer;

- (IBAction)categorySelectedBtnPressed:(id)sender;

- (IBAction)menuBtnPressed:(id)sender;
- (IBAction)previewButtonPressed:(id)sender;

@property (weak, nonatomic) IBOutlet UIButton *menuBtn;
@property (weak, nonatomic) IBOutlet UIImageView *maskingView;

@property (weak, nonatomic) IBOutlet UIButton *submitButton;
@property (weak, nonatomic) IBOutlet UIButton *categorySelectionButton;

@property (assign, nonatomic)BOOL isGuestFirstLoad;
- (void)hideMenuBtn;
@end
