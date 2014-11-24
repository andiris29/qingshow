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
#import "QSP02ModelDetailViewController.h"
#import "QSS03ShowDetailViewController.h"

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
    self.badgeView = [QSBadgeView generateViewWithType:self.type];
    [self.badgeContainer addSubview:self.badgeView];
    self.badgeView.delegate = self;

}

#pragma mark - Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self _configView];
    self.currentSection = 0;
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
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

#pragma mark - 
- (void)didClickShow:(NSDictionary*)showDict
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPeople:(NSDictionary *)peopleDict
{
    UIViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
}
#pragma mark - QSModelListTableViewDelegateObjDelegate
- (void)clickModel:(NSDictionary*)model
{
    UIViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:model];
    [self.navigationController pushViewController:vc animated:YES];
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

@end
