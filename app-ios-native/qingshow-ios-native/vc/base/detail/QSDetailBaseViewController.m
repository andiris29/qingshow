//
//  QSModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>

#import "QSDetailBaseViewController.h"
#import "QSBadgeView.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSP02ModelDetailViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "UIViewController+QSExtension.h"


@interface QSDetailBaseViewController ()
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstrain;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *backBtnTopConstrain;
@property (weak, nonatomic) IBOutlet UIView *badgeContainer;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;


#pragma mark - Data
@property (assign, nonatomic) int currentSection;

@property (assign, nonatomic) CGPoint touchLocation;
@property (strong, nonatomic) UIView* currentTouchView;

@property (assign, nonatomic) float backPreTopCon;

@end

@implementation QSDetailBaseViewController

- (void)_configView
{
    //badge view
    self.badgeView = [QSBadgeView generateViewWithType:self.type];
    [self.badgeContainer addSubview:self.badgeView];
    self.badgeView.delegate = self;

}

#pragma mark - Life Cycle
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    [self _configView];
    self.currentSection = 0;
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    self.backPreTopCon = self.backBtnTopConstrain.constant;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - QSModelBadgeViewDelegate
- (void)changeToSection:(int)section
{
    if (self.currentSection == section) {
        return;
    }
    UIScrollView* currentView = self.viewArray[self.currentSection];
    [currentView.superview bringSubviewToFront:currentView];
    CGPoint p = currentView.contentOffset;
    
    CATransition* transition = [[CATransition alloc] init];
    CATransition* transition2 = [[CATransition alloc] init];
    transition.type = kCATransitionPush;
    transition2.type = kCATransitionPush;
    if (self.currentSection < section) {
        transition.subtype = kCATransitionFromRight;
        transition2.subtype = kCATransitionFromRight;
    } else {
        transition.subtype = kCATransitionFromLeft;
        transition2.subtype = kCATransitionFromLeft;
    }

    [currentView.layer addAnimation:transition forKey:@"transition"];
    
    for (int i = 0; i < self.viewArray.count; i++) {
        UIScrollView* view = self.viewArray[i];
        view.hidden = i != section;
        if (p.y < 0.1) {
            view.contentOffset = p;
        }
    }
    
    self.currentSection = section;
    currentView = self.viewArray[self.currentSection];
    [currentView.layer addAnimation:transition2 forKey:@"transition"];

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
        if (self.topConstrain.constant < -182) {
            self.topConstrain.constant = -182;
            f = NO;
        }
        if (f) {
            scrollView.contentOffset = CGPointZero;
        }
        
        self.backBtnTopConstrain.constant = self.backPreTopCon + self.topConstrain.constant;
        [self.view layoutIfNeeded];
        
    }
}


//Virtual
- (void)singleButtonPressed
{
    
}

#pragma mark - 
- (void)didClickShow:(NSDictionary*)showDict
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPeople:(NSDictionary *)peopleDict
{
    [self showPeopleDetailViewControl:peopleDict];
}
#pragma mark - QSModelListTableViewDelegateObjDelegate
- (void)clickModel:(NSDictionary*)model
{
    [self showPeopleDetailViewControl:model];
}
- (void)followBtnPressed:(NSDictionary*)model
{
#warning 需要更新modelList内数据
    [SHARE_NW_ENGINE handleFollowModel:model onSucceed:^(BOOL fFollow) {
        if (fFollow) {
            [self showTextHud:@"follow succeed"];
        }
        else
        {
            [self showTextHud:@"unfollow succeed"];
        }
    } onError:^(NSError *error) {
        [self showErrorHudWithText:@"error"];
    }];
}

- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
@end
