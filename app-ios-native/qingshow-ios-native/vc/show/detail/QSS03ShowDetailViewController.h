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
#import "QSVideoBaseViewController.h"
#import "QSAbstractRootViewController.h"

@interface QSS03ShowDetailViewController : QSVideoBaseViewController < UIScrollViewDelegate, UIGestureRecognizerDelegate, QSShareViewControllerDelegate, UIAlertViewDelegate>

#pragma mark - IBOutlet
@property (weak, nonatomic) IBOutlet UILabel *modelNameLabel;
@property (weak, nonatomic) IBOutlet UIImageView *headIconImageView;
@property (weak, nonatomic) IBOutlet UILabel *bonusLabel;


@property (weak, nonatomic) IBOutlet UIButton *commentBtn;
@property (weak, nonatomic) IBOutlet UIButton *favorBtn;

@property (weak, nonatomic) IBOutlet UIView *buttnPanel;
@property (weak, nonatomic) IBOutlet UIView *discountContainer;


@property (weak, nonatomic) IBOutlet UIView *coverContainer;
@property (weak, nonatomic) IBOutlet UIImageView *coverBackgroundImageView;
@property (weak, nonatomic) IBOutlet UIImageView *coverForegroundImageView;
@property (weak, nonatomic) IBOutlet UIImageView *coverImageView;
@property (weak, nonatomic) IBOutlet UIView *coverLabelContainerView;
@property (weak, nonatomic) IBOutlet UILabel *releaseDateLabel;
@property (weak, nonatomic) IBOutlet UIButton *trashBtn;




#pragma mark - Init
- (instancetype)initWithShowId:(NSString*)showId;
- (instancetype)initWithShow:(NSDictionary*)showDict;

#pragma mark - IBAction
- (IBAction)commentBtnPressed:(id)sender;
- (IBAction)shareBtnPressed:(id)sender;
- (IBAction)likeBtnPressed:(id)sender;
- (IBAction)trashBtnPressed:(id)sender;

@property (weak, nonatomic) IBOutlet UIView *modelContainer;

@property (weak, nonatomic) IBOutlet UIButton *shareBtn;

@property (assign, nonatomic) BOOL showDeletedBtn;
@property (assign,nonatomic)BOOL showBackBtn;
@end
