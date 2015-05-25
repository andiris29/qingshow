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
@property (assign, nonatomic) CGPoint preTouchLocation;
@property (strong, nonatomic) UIView* currentTouchView;

@property (assign, nonatomic) float backPreTopCon;
@property (assign, nonatomic) BOOL canScrollBadgeViewUp;

@end

@implementation QSDetailBaseViewController

- (void)_configView
{
    //badge view
    self.badgeView = [QSBadgeView generateView];
    [self.badgeContainer addSubview:self.badgeView];
    self.badgeView.frame = self.badgeContainer.bounds;
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
    [self hideNaviBackBtnTitle];
    self.backPreTopCon = self.backBtnTopConstrain.constant;
    self.canScrollBadgeViewUp = YES;
    
    if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
}

- (void)viewWillLayoutSubviews
{
    [super viewWillLayoutSubviews];
    [self configContentInset];
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
    }
    
    self.currentSection = section;
    currentView = self.viewArray[self.currentSection];
    [currentView.layer addAnimation:transition2 forKey:@"transition"];


    float time = ABS(self.topConstrain.constant) / 800;
    self.topConstrain.constant = 0;
    self.backBtnTopConstrain.constant = self.backPreTopCon + self.topConstrain.constant;
    self.canScrollBadgeViewUp = NO;
    __weak QSDetailBaseViewController* weakSelf = self;
    [UIView animateWithDuration:time animations:^{
        [weakSelf.view layoutIfNeeded];
        
        CGPoint p = currentView.contentOffset;
        p.y = -currentView.contentInset.top;
        currentView.contentOffset = p;
    } completion:^(BOOL finished) {
        weakSelf.currentTouchView = nil;
        weakSelf.canScrollBadgeViewUp = YES;
    }];
}

#pragma mark - Scroll View

- (void)configContentInset
{
    float height = self.badgeView.frame.size.height;
    for (UIScrollView* view in self.viewArray) {
        view.contentInset = UIEdgeInsetsMake(height, 0, 0, 0);
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (!self.canScrollBadgeViewUp) {
        return;
    }
    float height = self.badgeContainer.frame.size.height;
    self.topConstrain.constant = - (scrollView.contentOffset.y + scrollView.contentInset.top);
    self.topConstrain.constant = self.topConstrain.constant < -height ? -height : self.topConstrain.constant;
    self.topConstrain.constant = self.topConstrain.constant > 0 ? 0 : self.topConstrain.constant;
    self.backBtnTopConstrain.constant = self.backPreTopCon + self.topConstrain.constant;
    [self.view layoutIfNeeded];
    return;
    /*
    __weak QSDetailBaseViewController* weakSelf = self;
    if (self.currentTouchView != scrollView) {
        self.currentTouchView = scrollView;
        self.touchLocation = scrollView.contentOffset;
        self.preTouchLocation = scrollView.contentOffset;
    }
    else
    {
        float deltaY = scrollView.contentOffset.y - self.touchLocation.y;
        float preDeltaY = scrollView.contentOffset.y - self.preTouchLocation.y;
        
        if (scrollView.contentOffset.y <= (- self.topConstrain.constant - self.badgeView.frame.size.height) && deltaY < 0)
        {
            //Scroll To Top
            self.touchLocation = CGPointMake(0, - self.badgeView.frame.size.height);
            self.preTouchLocation = self.touchLocation;
            self.topConstrain.constant = -scrollView.contentOffset.y - self.badgeView.frame.size.height;
            self.topConstrain.constant = self.topConstrain.constant >= 0 ? 0 : self.topConstrain.constant;
            self.backBtnTopConstrain.constant = self.backPreTopCon + self.topConstrain.constant;
            [self.view layoutIfNeeded];
            self.touchLocation = scrollView.contentOffset;
            return;
        }
        
        if ((deltaY >= 0 && self.canScrollBadgeViewUp)){
            if (scrollView.contentOffset.y > - self.badgeView.frame.size.height) {
                //move badgeView up
                self.topConstrain.constant -= deltaY;
                if (self.topConstrain.constant > 0) {
                    self.topConstrain.constant = 0;
                }
                if (self.topConstrain.constant < -self.badgeView.frame.size.height) {
                    self.topConstrain.constant = -self.badgeView.frame.size.height;
                }
                self.backBtnTopConstrain.constant = self.backPreTopCon + self.topConstrain.constant;
            }
            self.touchLocation = scrollView.contentOffset;
            self.preTouchLocation = self.touchLocation;
            //Back Btn

            [self.view layoutIfNeeded];
        }
        else if (preDeltaY < -[UIScreen mainScreen].bounds.size.height * 2){
            float time = ABS(self.topConstrain.constant - 0) / 600;
            self.topConstrain.constant = 0;
            self.backBtnTopConstrain.constant = self.backPreTopCon + self.topConstrain.constant;
            self.canScrollBadgeViewUp = NO;
            [UIView animateWithDuration:time animations:^{
                [weakSelf.view layoutIfNeeded];
            } completion:^(BOOL finished) {
                weakSelf.touchLocation = scrollView.contentOffset;
                weakSelf.preTouchLocation = weakSelf.touchLocation;
                weakSelf.canScrollBadgeViewUp = YES;
            }];
        } else {
            self.touchLocation = scrollView.contentOffset;
        }
    }
     */
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
//    [self showPeopleDetailViewControl:peopleDict];
}
#pragma mark - QSModelListTableViewProviderDelegate
- (void)clickModel:(NSDictionary*)model
{
//    [self showPeopleDetailViewControl:model];
}
- (void)followBtnPressed:(NSDictionary*)model
{
    [SHARE_NW_ENGINE handleFollowModel:model onSucceed:^(BOOL fFollow) {
        if (fFollow) {
            [self showTextHud:@"关注成功"];
        }
        else
        {
            [self showTextHud:@"取消关注成功"];
        }
    } onError:^(NSError *error) {
        [self showErrorHudWithText:@"error"];
    }];
}

- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
@end
