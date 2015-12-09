//
//  QSModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>
#import "QSBadgeButton.h"
#import "QSDetailBaseViewController.h"
#import "QSBadgeView.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSS03ShowDetailViewController.h"
#import "UIViewController+QSExtension.h"
#import "QSUnreadManager.h"
#import "QSUserManager.h"
#import "QSPeopleUtil.h"


@interface QSDetailBaseViewController ()
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstrain;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *backBtnTopConstrain;
@property (weak, nonatomic) IBOutlet UIView *badgeContainer;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;


#pragma mark - Data


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
    self.badgeView.btnGroup.delegate = self;

}

#pragma mark - Life Cycle
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self updateDot];
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
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    self.view.backgroundColor = [UIColor colorWithWhite:0.949 alpha:1.000];
    [self changeToSection:1];
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
    UIView *view = [self.viewArray lastObject];
    [currentView.superview bringSubviewToFront:currentView];
    [view.superview bringSubviewToFront:view];
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
    self.badgeView.touchDelegateView = currentView;
    [currentView.layer addAnimation:transition2 forKey:@"transition"];
    
    if ([UIScreen mainScreen].bounds.size.height > currentView.contentSize.height) {
        //currentView无法撑满整个屏幕，需要下移
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
    } else {
        if (currentView.contentOffset.y + currentView.contentInset.top < ABS(self.topConstrain.constant)) {
            CGPoint p = currentView.contentOffset;
            p.y = ABS(self.topConstrain.constant) - currentView.contentInset.top;
            currentView.contentOffset = p;
        }
    }
}

#pragma mark - Scroll View

- (void)configContentInset
{
    float height = self.badgeView.frame.size.height;
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.viewArray];
    [array removeLastObject];
    for (UIScrollView* view in array) {
    
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
- (void)didClickShow:(NSDictionary*)showDict provider:(QSAbstractListViewProvider *)provider
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPeople:(NSDictionary *)peopleDict provider:(QSAbstractListViewProvider *)provider
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

#pragma mark -
- (void)btnGroup:(QSBadgeBtnGroup*)btnGroup didSelectType:(QSBadgeButtonType)type {
    [self changeToSection:type];
    [self updateDot];
}

- (void)updateDot {
     QSBadgeButton* btn = [self.badgeView.btnGroup findBtnOfType:QSBadgeButtonTypeRecommend];
    if (btn) {
        btn.hasDot = [[QSEntityUtil getIdOrEmptyStr:[QSUserManager shareUserManager].userInfo] isEqualToString:[QSEntityUtil getIdOrEmptyStr:self.userInfo]] && [[QSUnreadManager getInstance] shouldShowRecommandUnread];
    }
}
@end
