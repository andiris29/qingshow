//
//  QSModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSDetailBaseViewController.h"
#import "QSBadgeView.h"
#import "QSNetworkEngine.h"
#import "UIViewController+ShowHud.h"

@interface QSDetailBaseViewController ()
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstrain;
@property (weak, nonatomic) IBOutlet UIView *badgeContainer;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;


#pragma mark - Data
@property (assign, nonatomic) int currentSection;

@property (assign, nonatomic) CGPoint touchLocation;
@property (strong, nonatomic) UIView* currentTouchView;


@end

@implementation QSDetailBaseViewController

- (void)_configView
{
    //badge view
    self.badgeView = [QSBadgeView generateView];
    [self.badgeContainer addSubview:self.badgeView];
    self.badgeView.delegate = self;

}

#pragma mark - Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self _configView];
    self.currentSection = 0;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - QSModelBadgeViewDelegate
- (void)changeToSection:(int)section
{
    UIScrollView* currentView = self.viewArray[self.currentSection];
    CGPoint p = currentView.contentOffset;
    
    for (int i = 0; i < self.viewArray.count; i++) {
        UIScrollView* view = self.viewArray[i];
        view.hidden = i != section;
        if (p.y < 0.1) {
            view.contentOffset = p;
        }
    }
    
    self.currentSection = section;
}

#pragma mark - Scroll View
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.currentTouchView != scrollView) {
        self.currentTouchView = scrollView;
        self.touchLocation = scrollView.contentOffset;
    }
    else
    {
        self.topConstrain.constant -= scrollView.contentOffset.y;
        BOOL f = YES;
        if (self.topConstrain.constant > 0) {
            self.topConstrain.constant = 0;
            f = NO;
        }
        if (self.topConstrain.constant < -115) {
            self.topConstrain.constant = -115;
            f = NO;
        }
        if (f) {
            scrollView.contentOffset = CGPointZero;
        }

        [self.view layoutIfNeeded];
        
    }
}


//Virtual
- (void)singleButtonPressed
{
    
}
@end
