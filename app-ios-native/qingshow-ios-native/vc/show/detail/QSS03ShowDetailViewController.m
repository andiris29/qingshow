//
//  QSS03ShowDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ShowDetailViewController.h"
#import "QSSingleImageScrollView.h"
#import "QSS04CommentListViewController.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "QSCommonUtil.h"
#import "QSDateUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSNetworkKit.h"
#import "QSItemUtil.h"
#import "QSImageNameUtil.h"
#import "QSPromotionUtil.h"

#import "UIViewController+ShowHud.h"
#import <QuartzCore/QuartzCore.h>
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"

#import "QSUserManager.h"
#import "QSU01UserDetailViewController.h"

#define PAGE_ID @"S03 - 秀"

@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) NSDictionary* showDict;

@property (strong, nonatomic) QSShareViewController* shareVc;
@end

@implementation QSS03ShowDetailViewController
- (void)setMenuProvider:(NSObject<QSMenuProviderDelegate> *)menuProvider {
    _menuProvider = menuProvider;
    self.menuBtn.hidden = _menuProvider == nil;
    self.backBtn.hidden = _menuProvider != nil;
}
#pragma mark - Init Method
- (id)initWithShow:(NSDictionary*)showDict
{
    self = [self initWithNibName:@"QSS03ShowDetailViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {

    [super viewDidLoad];
    self.menuProvider = self.menuProvider;
    self.shareVc = [[QSShareViewController alloc] init];
    self.shareVc.delegate = self;
    [self.view addSubview:self.shareVc.view];
    self.shareVc.view.frame = self.view.bounds;
    [self bindWithDict:self.showDict];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.bounds.size.height / 2;
    self.headIconImageView.layer.masksToBounds = YES;
    self.headIconImageView.userInteractionEnabled = YES;
    [self.headIconImageView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapHeadIcon:)]];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (!self.itemListVc) {
        __weak QSS03ShowDetailViewController* weakSelf = self;
        if (self.showDict) {
            [weakSelf bindExceptImageWithDict:self.showDict];
        }
        [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
            weakSelf.showDict = dict;
            [weakSelf bindExceptImageWithDict:dict];
            NSDictionary* promotionDict = [QSShowUtil getPromotionRef:dict];
            if (!promotionDict) {
                self.discountContainer.hidden = YES;
            } else {
                if (![QSPromotionUtil getIsEnabled:promotionDict]) {
                    [self showDiscountContainer];
                } else {
                    self.discountContainer.hidden = YES;
                }
            }
        } onError:^(NSError *error) {
            
        }];
    }

    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
}


- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideSharePanel];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
#warning @mhy 在这里改边缘尺寸
    float topMargin = 10.f;
    float leftMargin = 10.f;
    float rightMargin = 10.f;
    float bottomMargin = 10.f;
    
    self.coverImageView.frame =
    CGRectMake(
               self.coverForegroundImageView.frame.origin.x + leftMargin,
               self.coverForegroundImageView.frame.origin.y + topMargin,
               self.coverForegroundImageView.frame.size.width - leftMargin - rightMargin,
               self.coverForegroundImageView.frame.size.height - topMargin - bottomMargin);
    
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)dict
{
    [self bindExceptImageWithDict:dict];
    
    NSArray* array = [self generateImagesData];
    if (array && array.count) {
        self.scrollViewContainer.hidden = NO;
        self.coverContainer.hidden = YES;
        [self updateShowImgScrollView];
    } else {
        self.scrollViewContainer.hidden = YES;
        self.coverContainer.hidden = NO;
        [self updateCover];
    }

}
- (void)updateCover {
    [self.coverImageView setImageFromURL:[QSShowUtil getCoverUrl:self.showDict]];
    [self.coverForegroundImageView setImageFromURL:[QSShowUtil getCoverForegroundUrl:self.showDict]];
    [self.coverBackgroundImageView setImageFromURL:[QSShowUtil getCoverBackgroundUrl:self.showDict]];
}

- (void)bindExceptImageWithDict:(NSDictionary*)dict
{    
    self.playBtn.hidden = !self.generateVideoPath;

    //Like Btn
    [self setLikeBtnHover:[QSShowUtil getIsLike:dict]];
    
    [self.commentBtn setTitle:[QSShowUtil getNumberCommentsDescription:dict] forState:UIControlStateNormal];
    [self.favorBtn setTitle:[QSShowUtil getNumberLikeDescription:dict] forState:UIControlStateNormal];
    [self.itemBtn setTitle:[QSShowUtil getNumberItemDescription:self.showDict] forState:UIControlStateNormal];
    
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:self.showDict];
    if (!peopleDict) {
        self.headIconImageView.hidden = YES;
        self.modelNameLabel.hidden = YES;
    } else {
        NSDictionary* currentUser = [QSUserManager shareUserManager].userInfo;
        if ([[QSCommonUtil getIdOrEmptyStr:currentUser] isEqualToString:[QSCommonUtil getIdOrEmptyStr:peopleDict]]) {
            //当前用户
            self.headIconImageView.hidden = YES;
            self.modelNameLabel.hidden = YES;
            self.releaseDateLabel.hidden = NO;
            self.trashBtn.hidden = NO;
            self.playBtn.hidden = YES;
            self.pauseBtn.hidden = YES;
            NSDate* createDate = [QSShowUtil getCreatedDate:dict];
            self.releaseDateLabel.text = [NSString stringWithFormat:@"发布日期：%@", [QSDateUtil buildDayStringFromDate:createDate]];
        } else {
            self.headIconImageView.hidden = NO;
            [self.headIconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict]];
            self.modelNameLabel.hidden = NO;
            self.modelNameLabel.text = [QSPeopleUtil getNickname:peopleDict];
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UI
- (void)setLikeBtnHover:(BOOL)fHover
{
    if (fHover) {
        [self.favorBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btn_full"] forState:UIControlStateNormal];
    } else {
        [self.favorBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btnIcon"] forState:UIControlStateNormal];
    }
}


#pragma mark - IBAction
- (IBAction)playOrPauseBtnPressed:(id)sender {
    [self hideSharePanel];
    [super playOrPauseBtnPressed:sender];
}

- (IBAction)commentBtnPressed:(id)sender {
    [self hideSharePanel];
    UIViewController* vc =[[QSS04CommentListViewController alloc] initWithShow:self.showDict];
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)shareBtnPressed:(id)sender {
    [self showSharePanel];
}


- (IBAction)likeBtnPressed:(id)sender {
    [self hideSharePanel];
    NSDictionary* showDict = self.showDict;
    [SHARE_NW_ENGINE handleShowLike:showDict onSucceed:^(BOOL f) {
        if (f) {
            [self showSuccessHudWithText:@"添加收藏"];
            [self.favorBtn setTitleColor:[UIColor colorWithRed:0.894 green:0.310 blue:0.392 alpha:1.000] forState:UIControlStateNormal];
            
        } else {
            [self showSuccessHudWithText:@"取消收藏"];
            [self.favorBtn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
        }
        [self bindExceptImageWithDict:showDict];
        
    } onError:^(NSError *error) {
        [self handleError:error];
        [self bindExceptImageWithDict:showDict];
    }];
}

#pragma mark -
- (IBAction)itemButtonPressed:(id)sender {
    QSS07ItemListViewController* vc = [[QSS07ItemListViewController alloc] initWithShow:self.showDict];
    vc.delegate = self;
    self.itemListVc = vc;
    vc.view.frame = self.view.bounds;
    [self.view addSubview:vc.view];
    [self setPlayModeBtnsHidden:YES];
    [self addChildViewController:vc];
    vc.view.alpha = 0.f;
    [UIView animateWithDuration:.3f animations:^{
        vc.view.alpha = 1.f;
    }];
    
}

- (IBAction)menuBtnPressed:(id)sender {
    [self.menuProvider didClickMenuBtn];
}

- (IBAction)trashBtnPressed:(id)sender {
    [SHARE_NW_ENGINE matcherHide:self.showDict onSucceed:^{
        [self showTextHud:@"删除成功"];
        [self performSelector:@selector(backBtnPressed:) withObject:nil afterDelay:TEXT_HUD_DELAY];
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}


- (void)didClickItemListCloseBtn
{
    [UIView animateWithDuration:.3f animations:^{
        self.itemListVc.view.alpha = 0.f;
    } completion:^(BOOL finished) {
        [self setPlayModeBtnsHidden:NO];
        [self.itemListVc.view removeFromSuperview];
        [self.itemListVc removeFromParentViewController];
        self.itemListVc = nil;
    }];

}

-(void)playMovie:(NSString *)path{
    [self hideSharePanel];
    [super playMovie:path];
}

#pragma mark - Share
- (void)showSharePanel
{
    NSString* showId = [QSCommonUtil getIdOrEmptyStr:self.showDict];
    
//    NSString* urlStr = [NSString stringWithFormat:@"http://121.41.161.239/app-web?action=shareShow&_id=%@", showId];
    
    //获取userID
    QSUserManager *um = [QSUserManager shareUserManager];
    NSString* peopleID = um.userInfo[@"_id"];
    
    NSString *urlStr = [NSString stringWithFormat:@"http://chingshow.com/app-web?action=shareShow&_id=%@&people.id=%@",showId,peopleID];
    [self.shareVc showSharePanelWithUrl:urlStr];
}
- (void)hideSharePanel
{
    [self.shareVc hideSharePanel];
}
- (void)didShareWeiboSuccess
{

    [SHARE_NW_ENGINE didShareShow:self.showDict onSucceed:^{
        [self showSuccessHudWithText:@"分享成功"];
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}
- (void)didShareWechatSuccess {
    [SHARE_NW_ENGINE didShareShow:self.showDict onSucceed:^{
        [self showSuccessHudWithText:@"分享成功"];
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}

#pragma mark - Override
#pragma mark Data
- (NSArray*)generateImagesData
{
    return [QSShowUtil getShowVideoPreviewUrlArray:self.showDict];;
}
- (NSString*)generateVideoPath
{
    return [QSShowUtil getVideoPath:self.showDict];
}

#pragma mark Btn
- (void)setPlayModeBtnsHidden:(BOOL)hidden
{
    self.backBtn.hidden = hidden;
    [self setBtnsHiddenExceptBackAndPlay:hidden];
    
}
- (void)setBtnsHiddenExceptBack:(BOOL)hidden
{
    [self setBtnsHiddenExceptBackAndPlay:hidden];
    self.playBtn.hidden = hidden;
//    self.pauseBtn.hidden = hidden;
}
- (void)setBtnsHiddenExceptBackAndPlay:(BOOL)hidden
{
    self.buttnPanel.hidden = hidden;
    self.modelContainer.hidden = hidden;
}

#pragma mark Mob
- (void)logMobPlayVideo:(NSTimeInterval)playbackTime
{
    [MobClick event:@"playVideo" attributes:@{@"showId" : self.showDict[@"_id"], @"length": @(playbackTime).stringValue} durations:(int)(playbackTime * 1000)];
}

#pragma mark - Discount
- (void)hideDiscountContainer {
    __weak QSS03ShowDetailViewController* weakSelf = self;
    [UIView animateWithDuration:.5f animations:^{
        weakSelf.discountContainer.alpha = 0;
    } completion:^(BOOL finished) {
        weakSelf.discountContainer.hidden = YES;
    }];

}
- (void)didTapHeadIcon:(id)sender {
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:self.showDict];
    if (peopleDict) {
        QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDict];
        vc.menuProvider = self.menuProvider;
        [self.navigationController pushViewController:vc animated:YES];
    }
}


- (void)showDiscountContainer{
    self.discountContainer.hidden = NO;
    self.discountContainer.alpha = 1.f;
    [self performSelector:@selector(hideDiscountContainer) withObject:nil afterDelay:5.f];
}
@end
