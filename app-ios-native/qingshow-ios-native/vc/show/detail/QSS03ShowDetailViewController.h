//
//  QSS03ShowDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSImageScrollViewBase.h"
#import "QSShareViewController.h"
#import "QSS07ItemListViewController.h"
#import "QSVideoBaseViewController.h"


@interface QSS03ShowDetailViewController : QSVideoBaseViewController < UIScrollViewDelegate, UIGestureRecognizerDelegate, QSShareViewControllerDelegate, QSS03ItemListViewControllerDelegate>

#pragma mark - IBOutlet
@property (weak, nonatomic) IBOutlet UILabel *contentLabel;
@property (weak, nonatomic) IBOutlet UIButton *commentBtn;
@property (weak, nonatomic) IBOutlet UIButton *favorBtn;

@property (weak, nonatomic) IBOutlet UIView *buttnPanel;
@property (weak, nonatomic) IBOutlet UIButton *itemBtn;
@property (weak, nonatomic) IBOutlet UIView *discountContainer;

#pragma mark - Init
- (id)initWithShow:(NSDictionary*)showDict;

#pragma mark - IBAction
- (IBAction)commentBtnPressed:(id)sender;
- (IBAction)shareBtnPressed:(id)sender;
- (IBAction)likeBtnPressed:(id)sender;
- (IBAction)itemButtonPressed:(id)sender;

@property (weak, nonatomic) IBOutlet UIView *modelContainer;

@property (weak, nonatomic) IBOutlet UIButton *shareBtn;

@property (strong, nonatomic) QSS07ItemListViewController* itemListVc;

@end
